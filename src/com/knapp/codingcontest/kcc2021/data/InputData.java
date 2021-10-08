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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class InputData {
  // ----------------------------------------------------------------------------

  private static final String PATH_INPUT_DATA;
  static {
    try {
      PATH_INPUT_DATA = new File("./data").getCanonicalPath();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  // ----------------------------------------------------------------------------

  private final String dataPath;

  private final Set<PalletType> palletTypes = new LinkedHashSet<PalletType>();
  private final List<Packet> packets = new LinkedList<Packet>();

  // ----------------------------------------------------------------------------

  public InputData() {
    this(InputData.PATH_INPUT_DATA);
  }

  public InputData(final String dataPath) {
    this.dataPath = dataPath;
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "InputData@" + dataPath + "[\n " + palletTypes + ",\n " + packets + "\n]";
  }

  // ----------------------------------------------------------------------------

  public void readData() throws IOException {
    readPalletTypes();
    readPackets();
  }

  // ----------------------------------------------------------------------------

  public final Set<PalletType> getPalletTypes() {
    return Collections.unmodifiableSet(palletTypes);
  }

  public final List<Packet> getPackets() {
    return packets;
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  private void readPalletTypes() throws IOException {
    final Reader fr = new FileReader(fullFileName("pallet-types.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // id;length;width;maxHeight;maxWeight;
        final String[] columns = splitCsv(line);
        final String id = columns[0];
        final int length = Integer.parseInt(columns[1]);
        final int width = Integer.parseInt(columns[2]);
        final int maxHeight = Integer.parseInt(columns[3]);
        final int maxWeight = Integer.parseInt(columns[4]);
        final PalletType palletType = new PalletType(id, length, width, maxHeight, maxWeight);
        palletTypes.add(palletType);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  private void readPackets() throws IOException {
    final Reader fr = new FileReader(fullFileName("packets.csv"));
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(fr);
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if ("".equals(line) || line.startsWith("#")) {
          continue;
        }
        // id;truck;length;width;weight;
        final String[] columns = splitCsv(line);
        final int id = Integer.parseInt(columns[0]);
        final int truckId = Integer.parseInt(columns[1]);
        final int length = Integer.parseInt(columns[2]);
        final int width = Integer.parseInt(columns[3]);
        final int weight = Integer.parseInt(columns[4]);
        final Packet packet = new Packet(id, truckId, length, width, weight);
        packets.add(packet);
      }
    } finally {
      close(reader);
      close(fr);
    }
  }

  // ----------------------------------------------------------------------------

  protected File fullFileName(final String fileName) {
    final String fullFileName = dataPath + File.separator + fileName;
    return new File(fullFileName);
  }

  protected void close(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (final IOException exception) {
        exception.printStackTrace(System.err);
      }
    }
  }

  // ----------------------------------------------------------------------------

  protected String[] splitCsv(final String line) {
    return line.split(";");
  }

  // ----------------------------------------------------------------------------
  // ............................................................................
}
