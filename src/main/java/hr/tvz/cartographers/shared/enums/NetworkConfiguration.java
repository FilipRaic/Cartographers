package hr.tvz.cartographers.shared.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NetworkConfiguration {

    RMI_PORT("rmi.port"),
    PLAYER_ONE_SERVER_PORT("player.one.server.port"),
    PLAYER_TWO_SERVER_PORT("player.two.server.port"),
    HOSTNAME("hostname");

    private final String value;
}
