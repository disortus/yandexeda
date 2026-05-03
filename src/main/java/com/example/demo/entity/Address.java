import org.locationtech.jts.geom.Point;
import jakarta.persistence.Column;

@Entity
@Table(name="adresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "address_text", nullable = false)
    private String addressText;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point location;

    public Address() {
    }

    public Address(String addressText){
        this.addressText = addressText;
    }

    public Long getId() {
        return id;
    }

    public String getAddressText(){
        return addressText;
    }

    public void setAddressText(String addressText){
        this.addressText = addressText;
    }
}