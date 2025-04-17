package hr.tvz.cartographers.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class GameMove implements Serializable {

    private CellState[][] move;
}
