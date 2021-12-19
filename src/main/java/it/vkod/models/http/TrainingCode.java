package it.vkod.models.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrainingCode {

    QUERY("training_code");

    private final String name;

}
