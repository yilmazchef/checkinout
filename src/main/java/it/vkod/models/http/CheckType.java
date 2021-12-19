package it.vkod.models.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CheckType {

    IN("check_type", "IN"),
    OUT("check_type", "OUT");

    private final String name;
    private final String value;

}
