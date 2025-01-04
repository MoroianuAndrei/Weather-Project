package org.example.data_source.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter @Setter
@Entity
@Table(name = "weather")
public class WeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_weat")
    private int idWeat;

    @ManyToOne
    @JoinColumn(name = "id_loc", referencedColumnName = "id_loc")
    private LocationEntity location;  // Se face legătura cu LocationEntity prin id_loc

    @Column(name = "date")
    private Date date;

    @Column(name = "temperature")
    private String temperature;

    @Column(name = "condition")
    private String condition;
}
