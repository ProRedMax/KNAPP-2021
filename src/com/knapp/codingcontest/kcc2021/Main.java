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

package com.knapp.codingcontest.kcc2021;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.knapp.codingcontest.kcc2021.core.PrepareUpload;
import com.knapp.codingcontest.kcc2021.core.WarehouseInternal;
import com.knapp.codingcontest.kcc2021.data.InputData;
import com.knapp.codingcontest.kcc2021.solution.Solution;
import com.knapp.codingcontest.kcc2021.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2021.warehouse.WarehouseInfo;

/**
 * ----------------------------------------------------------------------------
 * you may change any code you like
 *   => but changing the output may lead to invalid results!
 * ----------------------------------------------------------------------------
 */
public class Main {
  // ----------------------------------------------------------------------------

  public static void main(final String... args) throws Exception {
    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
    System.out.println("vvv   KNAPP Coding Contest 2021: STARTING...   vvv");
    System.out.println(String.format("vvv                %s                    vvv", Main.DATE_FORMAT.format(new Date())));
    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... LOADING INPUT ...");
    final InputData input = new InputData();
    input.readData();

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RUN YOUR SOLUTION ...");
    final long start = System.currentTimeMillis();
    final WarehouseInternal iwarehouse = new WarehouseInternal(input);
    final Warehouse warehouse = iwarehouse;
    final Solution solution = new Solution(warehouse, input);
    Throwable throwable = null;
    try {
      solution.run();
    } catch (final Throwable _throwable) {
      throwable = _throwable;
    }
    final long end = System.currentTimeMillis();
    System.out.println("");
    System.out.println("# ... DONE ... (" + (end - start) + "ms)");

    System.out.println("");
    System.out.println("# ------------------------------------------------");
    System.out.println("# ... RESULT/COSTS FOR YOUR SOLUTION ...");
    System.out.println("#     " + solution.getParticipantName() + " / " + solution.getParticipantInstitution());

    if (throwable != null) {
      System.out.println("");
      System.out.println("# ... Ooops ...");
      System.out.println("");
      throwable.printStackTrace(System.out);
    } else {
      Main.printResults(warehouse);

      System.out.println("");
      System.out.println("# ------------------------------------------------");
      System.out.println("# ... WRITING OUTPUT/RESULT ...");
      PrepareUpload.createZipFile(iwarehouse, solution);
      System.out.println("");
      System.out
          .println(">>> Created " + PrepareUpload.FILENAME_WAREHOUSE_OPERATIONS + " & " + PrepareUpload.FILENAME_UPLOAD_ZIP);

      System.out.println("");
      System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
      System.out.println("^^^   KNAPP Coding Contest 2021: FINISHED      ^^^");
      System.out.println(String.format("^^^                %s                    ^^^", Main.DATE_FORMAT.format(new Date())));
      System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
    System.out.println("");
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  @SuppressWarnings("boxing")
  private static void printResults(final Warehouse warehouse) throws Exception {
    final WarehouseInfo info = warehouse.getInfo();

    final long pc = info.getPalletCount();
    final int up = info.getUnfinishedPacketCount();
    final long pa = info.getPalletsArea();
    final long pvu = info.getPalletsVolumeUsed();

    final long upc = info.getUnfinishedPacketsCost();
    final long pac = info.getPalletsAreaCost();
    final long pvuc = info.getPalletsVolumeUsedCost();
    final long tc = info.getTotalCost();

    System.out.println("");
    System.out.println(String.format("  ----------------------------- : ------------ | ----------------------"));
    System.out.println(String.format("      what                      :       costs  |  (details: count,...)"));
    System.out.println(String.format("  ----------------------------- : ------------ | ----------------------"));
    System.out.println(String.format("   -> number of pallets used    :  %10s  |  %6d#", "", pc));
    System.out.println(String.format("   -> unfinished packets        :  %10d  |  %6d#", upc, up));
    System.out.println(String.format("   -> area of pallets           :  %10d  |  %6d#", pac, pa));
    System.out.println(String.format("   -> used volume of pallets    :  %10d  |  %6d#  ", pvuc, pvu));
    System.out.println(String.format("  ----------------------------- : ------------ | ----------------------"));
    System.out.println("");
    System.out.println(String.format("   => TOTAL COST                   %10d", tc));
    System.out.println(String.format("                                  ============"));
  }

  // ----------------------------------------------------------------------------

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

  // ----------------------------------------------------------------------------

}
