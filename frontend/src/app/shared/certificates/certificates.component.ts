import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {DenialReason} from "../model/DenialReason";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {NotificationsService} from "../notifications.service";
import {CertificateService} from "../certificate.service";
import {CertificateDetailsWithUserInfo} from "../model/CertificateDetailsWithUserInfo";
import {NgxFileDropEntry} from "ngx-file-drop";

@Component({
  selector: 'app-certificates',
  templateUrl: './certificates.component.html',
  styleUrls: ['./certificates.component.css']
})
export class CertificatesComponent implements AfterViewInit {
  displayedColumns: string[] = ['serialNumber', 'certificateType', 'owner.fullName', 'organizationData', 'validFrom',
    'validUntil'];
  enableStatusChange: boolean = false;
  dataSource!: MatTableDataSource<CertificateDetailsWithUserInfo>;
  certificates: CertificateDetailsWithUserInfo[] = [];
  selectedCertificate!: CertificateDetailsWithUserInfo;
  denialReason: DenialReason = {
    denialReason: ''
  };

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;
  cindex: number = -1;

  constructor(private notificationService: NotificationsService,
              private certificateService: CertificateService) {
  }

  ngAfterViewInit(): void {
    this.certificateService.getAll().subscribe((res) => {
      this.certificates = res;
      this.dataSource = new MatTableDataSource<CertificateDetailsWithUserInfo>(this.certificates);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sortingDataAccessor = (item, property) => {
        switch (property) {
          case 'owner.fullName':
            return item.owner.fullName;
          case 'organizationData':
            return item.organizationData.name + ', ' + item.organizationData.unit + ', ' + item.organizationData.countryCode;
          default: // @ts-ignore
            return item[property];
        }
      };
      this.dataSource.sort = this.sort;
    });
  }

  public validate(): void {
    this.certificateService.validate(this.selectedCertificate.serialNumber).subscribe(
      result => {
        if (result) {
          this.notificationService.createNotification('Certificate is valid!');
        } else {
          this.notificationService.createNotification('Certificate is not valid!');
        }
      }
    )
  }

  public download(): void {
    this.certificateService.download(this.selectedCertificate.serialNumber).subscribe(
      result => {
        const file = new Blob([result], {type: 'application/octet-stream'});
        const fileReader = new FileReader();
        fileReader.onload = () => {
          const url = URL.createObjectURL(file);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'certificate' + this.selectedCertificate.serialNumber + '.crt';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          URL.revokeObjectURL(url);
        };
        fileReader.readAsArrayBuffer(file);
      }
    );
  }

  public refreshUI(certificate: CertificateDetailsWithUserInfo, index: number): void {
    this.cindex = index;
    if (!certificate) {
      this.enableStatusChange = false;
      return;
    }
    this.selectedCertificate = certificate;
    this.enableStatusChange = true;
  }

  files: NgxFileDropEntry[] = [];

  onFileDropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          if (file.type != "application/x-x509-ca-cert") {
            alert('Please drop only certificate files. ' + file.name + ' is not a certificate file.')
            return;
          }
          if (file.size > 1000000) {
            alert('File ' + file.name + ' is too big. Max size is 1MB.')
            return;
          }
          const formData = new FormData()
          formData.append('file', file, droppedFile.relativePath)

          this.certificateService.validateFormData(formData).subscribe({
              next: response => {
                if (response) {
                  alert(file.name + ' is a VALID certificate file.');
                } else {
                  alert(file.name + ' is NOT a valid certificate file.');
                }
              },
              error: error => {
                alert(file.name + ' is NOT a valid certificate file.');
              }
          });
        });
      } else {
        alert('Please drop only certificate files. ' + droppedFile.relativePath + ' is not a certificate file.');
      }
    }
  }
}
