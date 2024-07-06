package ro.upb.iotcoreservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MeasurementFilter {

    @NotNull
    private String userId;
    @NotNull
    private String measurement;
    private String startTime;
    private String endTime;
}
