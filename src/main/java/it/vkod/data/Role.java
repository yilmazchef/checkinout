package it.vkod.data;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@FieldDefaults( makeFinal = true, level = AccessLevel.PRIVATE )
public enum Role {

	USER( "user" ), ADMIN( "admin" );

	String title;

}
