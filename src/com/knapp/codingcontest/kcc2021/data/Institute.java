/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2021.data;

public enum Institute {
  // ----------------------------------------------------------------------------

  HTBLA_Kaindorf_Sulm, //
  HTBLuVA_Pinkafeld, //
  HTL_Villach, //
  HTL_Rennweg_Wien, //
  HTL_Wien_West, //
  HTL_Weiz, //
  ;

  // ----------------------------------------------------------------------------

  public static Institute find(final String _institute) {
    return Institute.valueOf(_institute);
  }

  // ----------------------------------------------------------------------------
}
