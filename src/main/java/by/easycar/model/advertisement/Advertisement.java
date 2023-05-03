package by.easycar.model.advertisement;

import by.easycar.model.user.UserForAd;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data

@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ad_sequence")
    @SequenceGenerator(catalog = "sequences", name = "ad_sequence", sequenceName = "advertisements_sequence_id", initialValue = 1, allocationSize = 1)
    @Column(name = "ad_id")
    private Long id;

    @Column(name = "ad_creation_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate creationDate;

    @Column(name = "ad_up_time", nullable = false)
    private LocalDateTime upTime = LocalDateTime.now();

    @Column(name = "ad_moderated", nullable = false)
    private boolean moderated = false;

    @Column(name = "ad_count_views", nullable = false)
    private Integer viewsCount = 0;

    @Column(name = "ad_price", nullable = false)
    private Integer price;

    @Column(name = "ad_car_year", nullable = false)
    private Integer carYear;

    @Column(name = "ad_vin", nullable = false)
    private String VINNumber;

    @Column(name = "ad_description", length = 1200, nullable = false)
    private String description;

    @Column(name = "ad_region", nullable = false)
    private String region;

    @Column(name = "ad_mileage", nullable = false)
    private Integer mileage;

    @Column(name = "ad_engine_capacity", nullable = false)
    private Integer engineCapacity;

    @Column(name = "ad_engine_type", nullable = false)
    private String engineType;

    @Column(name = "ad_transmission_type", nullable = false)
    private String transmissionType;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "v_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "u_id", nullable = false)
    private UserForAd user;

    @Embedded
    private ImageData imageData;
}
