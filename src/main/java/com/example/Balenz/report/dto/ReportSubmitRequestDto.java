package com.example.Balenz.report.dto;

import com.example.Balenz.report.entity.ProblemType;
import com.example.Balenz.report.entity.ReportTargetType;
import jakarta.validation.constraints.NotNull;

public record ReportSubmitRequestDto(

        @NotNull
        ReportTargetType targetType,

        @NotNull
        Long targetId,

        @NotNull
        ProblemType problemType,

        String content

) {
}
