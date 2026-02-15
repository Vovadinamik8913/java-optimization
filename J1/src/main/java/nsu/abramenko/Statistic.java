package nsu.abramenko;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Statistic {
    private final double timeSeconds;
    private final long maxMemory;
    private final long totalMemory;
    private final long usedMemory;
    private final long freeMemory;
}
