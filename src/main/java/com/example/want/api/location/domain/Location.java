package com.example.want.api.location.domain;

import com.example.want.api.location.dto.LocationResDto;
import com.example.want.api.state.domain.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    private Long popularCount;
    private String placeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "state_id")
    private State state;

    public void updatePopularCount(Long popularCount) {
        Location location = Location.builder()
                .popularCount(popularCount)
                .build();
    }

    public LocationResDto fromEntity(Location location) {
        LocationResDto dto = LocationResDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .popularCount(location.getPopularCount())
                .placeName(location.getPlaceName())
                .build();
        return dto;
    }
}