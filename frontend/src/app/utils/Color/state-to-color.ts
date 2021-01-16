import {State} from "../../models/state";
import {ColorCodes} from "./color-codes";


export function stateToColor(state: State): string {
  switch (state) {
    case State.CORRECT:
      return ColorCodes.SUCCESS;
    case State.MISSING:
      return ColorCodes.WARNING;
    case State.FAULTY:
      return ColorCodes.DANGER;
  }
}
