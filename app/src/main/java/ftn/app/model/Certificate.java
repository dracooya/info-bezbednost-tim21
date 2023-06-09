package ftn.app.model;

import ftn.app.model.enums.CertificateType;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    @Column()
    private String issuerSerialNumber;

    @Column(nullable = false)
    private String organizationData;

    @Column(nullable = false)
    private boolean isValid;

    @Column(nullable = false)
    private Date validFrom;

    @Column(nullable = false)
    private Date validUntil;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CertificateType certificateType;

    @Column(nullable = false)
    private String ownerEmail;

    @Column
    private String withdrawingReason;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Certificate certificate = (Certificate) o;
        return id != null && Objects.equals(id, certificate.id);
    }
}
