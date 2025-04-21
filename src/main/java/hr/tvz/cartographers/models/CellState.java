package hr.tvz.cartographers.models;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CellState implements Serializable {

    private String style;
}
