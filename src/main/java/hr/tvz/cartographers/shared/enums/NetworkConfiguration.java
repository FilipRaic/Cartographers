package hr.tvz.cartographers.shared.enums;

import hr.tvz.cartographers.CartographersApplication;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NetworkConfiguration {

    RMI_PORT("rmi.port"),
    PLAYER_ONE_SERVER_PORT("player.one.server.port"),
    PLAYER_TWO_SERVER_PORT("player.two.server.port"),
    HOSTNAME("hostname");

    private final String value;

    public static Optional<NetworkConfiguration> getPlayerPortConfiguration() {
        Player player = CartographersApplication.getPlayer();

        return Optional.ofNullable(switch (player) {
            case PLAYER_ONE -> PLAYER_ONE_SERVER_PORT;
            case PLAYER_TWO -> PLAYER_TWO_SERVER_PORT;
            default -> null;
        });
    }
}
