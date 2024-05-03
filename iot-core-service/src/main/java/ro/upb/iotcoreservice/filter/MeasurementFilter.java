package ro.upb.iotcoreservice.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MeasurementFilter {

    @NotNull
    private String userId;
    @NotNull
    private String measurement;
    private Instant startTime;
    private Instant endTime;
}
