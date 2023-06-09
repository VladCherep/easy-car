package by.easycar.model;

import by.easycar.model.user.UserPrivate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
@Immutable
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_sequence")
    @SequenceGenerator(name = "p_sequence", sequenceName = "payments_p_id_seq", allocationSize = 1)
    @Column(name = "p_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    @JsonBackReference
    private UserPrivate user;

    @Column(name = "p_date_time")
    @CreationTimestamp
    private LocalDateTime payTime;

    @Column(name = "p_bank_name")
    private String bankName;

    @Column(name = "p_operation_name")
    private String operationName;

    @Column(name = "p_transaction_number")
    private String transactionNumber;
}