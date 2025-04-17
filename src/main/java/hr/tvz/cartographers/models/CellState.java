package hr.tvz.cartographers.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CellState implements Serializable {

    private String style;
    private boolean hasCoin;
    private boolean isRuins;
}
