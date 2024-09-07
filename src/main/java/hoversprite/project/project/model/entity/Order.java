package hoversprite.project.project.model.entity;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "spray_session_id", nullable = false)
    private SpraySession spraySession;

    @Column(nullable = false)
    private String cropType;

    @Column(nullable = false)
    private Double area;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private Double totalCost;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SpraySession getSpraySession() {
        return spraySession;
    }

    public void setSpraySession(SpraySession spraySession) {
        this.spraySession = spraySession;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}
