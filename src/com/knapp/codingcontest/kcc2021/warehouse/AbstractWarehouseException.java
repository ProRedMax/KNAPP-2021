/* -*- java -*-
# =========================================================================== #
#                                                                             #
#                         Copyright (C) KNAPP AG                              #
#                                                                             #
#       The copyright to the computer program(s) herein is the property       #
#       of Knapp.  The program(s) may be used   and/or copied only with       #
#       the  written permission of  Knapp  or in  accordance  with  the       #
#       terms and conditions stipulated in the agreement/contract under       #
#       which the program(s) have been supplied.                              #
#                                                                             #
# =========================================================================== #
*/

package com.knapp.codingcontest.kcc2021.warehouse;

import com.knapp.codingcontest.kcc2021.data.Pallet;

public abstract class AbstractWarehouseException extends Exception {
  private static final long serialVersionUID = 1L;

  // ----------------------------------------------------------------------------

  AbstractWarehouseException(final String message) {
    super(message);
  }

  // ----------------------------------------------------------------------------

  static String shortString(final Pallet pallet) {
    final StringBuilder sb = new StringBuilder();
    sb.append("\n  Pallet[id=").append(pallet.getId());
    sb.append(", truckId=").append(pallet.getTruckId());
    sb.append(", ").append(pallet.getType());
    sb.append(", currentStackedHeight=").append(pallet.getCurrentStackedHeight());
    sb.append(", currentWeight=").append(pallet.getCurrentWeight());
    sb.append(", [");
    for (int l = 0; l < pallet.getType().getMaxHeight(); l++) {
      sb.append(pallet.getLayer(l).toString());
    }
    sb.append("\n  ]]\n");
    return sb.toString();
  }

  static class MyPallet extends Pallet {
    private static final long serialVersionUID = 1L;

    MyPallet(final Pallet other) {
      super(other.getId(), other.getTruckId(), other.getType());
    }
  }
}
