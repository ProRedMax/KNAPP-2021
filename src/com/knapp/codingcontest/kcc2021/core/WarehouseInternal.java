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

package com.knapp.codingcontest.kcc2021.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.knapp.codingcontest.kcc2021.data.InputData;
import com.knapp.codingcontest.kcc2021.data.Packet;
import com.knapp.codingcontest.kcc2021.data.Pallet;
import com.knapp.codingcontest.kcc2021.data.PalletType;
import com.knapp.codingcontest.kcc2021.warehouse.HeightExceededException;
import com.knapp.codingcontest.kcc2021.warehouse.PacketAlreadyUsedException;
import com.knapp.codingcontest.kcc2021.warehouse.PalletExtendsViolatedException;
import com.knapp.codingcontest.kcc2021.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2021.warehouse.WarehouseCostFactors;
import com.knapp.codingcontest.kcc2021.warehouse.WarehouseInfo;
import com.knapp.codingcontest.kcc2021.warehouse.WeightExceededException;
import com.knapp.codingcontest.kcc2021.warehouse.WrongTruckException;

public class WarehouseInternal implements Warehouse, WarehouseInfo {
  // ----------------------------------------------------------------------------

  public final Set<Integer> truckIds = new HashSet<Integer>();
  private final Map<Integer, PacketInfo> packetInfos = new HashMap<Integer, PacketInfo>();
  private final WarehouseCostFactors costFactors = new WarehouseInternal.CostFactors();
  private final List<PalletInternal> pallets = new ArrayList<PalletInternal>();
  private final List<WarehouseOperation> operations = new LinkedList<WarehouseOperation>();

  // ----------------------------------------------------------------------------

  public WarehouseInternal(final InputData input) {
    for (final Packet packet : input.getPackets()) {
      packetInfos.put(Integer.valueOf(packet.getId()), new PacketInfo(packet));
      truckIds.add(Integer.valueOf(packet.getTruckId()));
    }
  }

  // ----------------------------------------------------------------------------

  @Override
  public String toString() {
    return "WarehouseInternal[pallets=" + pallets + "]";
  }

  // ----------------------------------------------------------------------------
  // operations

  @Override
  public PalletInternal preparePallet(final int truckId, final PalletType palletType) {
    checkPreparePallet(truckId, palletType);
    return doPreparePallet(truckId, palletType);
  }

  @Override
  public int putPacket(final Pallet pallet, final Packet packet, final int x, final int y, final boolean rotated)
      throws PacketAlreadyUsedException //
      , WrongTruckException, WeightExceededException, PalletExtendsViolatedException //
      , HeightExceededException {
    final MyPacketPos pp = checkPutPacket(pallet, packet, x, y, rotated);
    doPutPacket((PalletInternal) pallet, pp, packet);
    return pp.layer;
  }

  // ----------------------------------------------------------------------------
  // info

  @Override
  public WarehouseInfo getInfo() {
    return this;
  }

  public WarehouseInfo getInfoSnapshot() {
    return new WarehouseInfoSnapshot(this);
  }

  @Override
  public WarehouseCostFactors getCostFactors() {
    return costFactors;
  }

  // ............................................................................

  @Override
  public Collection<Pallet> getPallets() {
    return Collections.unmodifiableList(pallets);
  }

  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  // WarehouseInfo

  @Override
  public final long getTotalCost() {
    return getPalletsAreaCost() + getPalletsVolumeUsedCost() + getUnfinishedPacketsCost();
  }

  // ............................................................................

  @Override
  public final long getUnfinishedPacketsCost() {
    if (getUnfinishedPacketCount() == 0) {
      return 0;
    }
    return (long) ((getUnfinishedPacketCount() * getCostFactors().getUnfinishedPacketCosts())
        + getCostFactors().getUnfinishedPacketsPenalty());
  }

  @Override
  public final long getPalletsAreaCost() {
    return (long) (getPalletsArea() * getCostFactors().getPalletAreaCosts());
  }

  @Override
  public final long getPalletsVolumeUsedCost() {
    return (long) (getPalletsVolumeUsed() * getCostFactors().getPalletVolumeUsedCosts());
  }

  // ----------------------------------------------------------------------------

  @Override
  public long getPalletCount() {
    return pallets.size();
  }

  @Override
  public long getPalletsArea() {
    long area = 0;
    for (final Pallet pallet : pallets) {
      area += area(pallet);
    }
    return area;
  }

  @Override
  public long getPalletsVolumeUsed() {
    long volumeUsed = 0;
    for (final Pallet pallet : pallets) {
      volumeUsed += volumeUsed(pallet);
    }
    return volumeUsed;
  }

  @Override
  public int getUnfinishedPacketCount() {
    int unfinishedPacketCount = 0;
    for (final PacketInfo pi : packetInfos.values()) {
      if (pi.pallet == null) {
        unfinishedPacketCount++;
      }
    }
    return unfinishedPacketCount;
  }

  // ............................................................................

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // INTERNAL HOUSEKEEPING OPERATIONS

  List<WarehouseInternal.WarehouseOperation> result() {
    return Collections.unmodifiableList(operations);
  }

  // ----------------------------------------------------------------------------

  @SuppressWarnings("unused")
  private void checkPreparePallet(final int truckId, final PalletType palletType) {
    if (!truckIds.contains(Integer.valueOf(truckId))) {
      throw new IllegalArgumentException("NOT FOUND: truckId=" + truckId + "\n");
    }
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  private PalletInternal doPreparePallet(final int truckId, final PalletType palletType) {
    final PalletInternal pallet = new PalletInternal(truckId, palletType);
    pallets.add(pallet);
    operations.add(new WarehouseInternal.PreparePallet(pallet));
    return pallet;
  }

  public static class PalletInternal extends Pallet {
    private static final long serialVersionUID = 1L;
    private static int PALLET_ID = 0;

    private PalletInternal(final int truckId, final PalletType type) {
      super(++PalletInternal.PALLET_ID, truckId, type);
      initLayers();
    }

    private void initLayers() {
      for (int l = 0; l < getType().getMaxHeight(); l++) {
        layers.put(Integer.valueOf(l), new LayerInternal(l));
      }
    }

    @Override
    public PalletInternal.LayerInternal getLayer(final int layer) {
      return (PalletInternal.LayerInternal) super.getLayer(layer);
    }

    public class LayerInternal extends Pallet.Layer {
      private LayerInternal(final int layer) {
        super(layer);
      }

      public void put(final PacketPos pp, final Packet packet) {
        currentStackedHeight = Math.max(currentStackedHeight, getLayer() + 1);
        currentWeight += packet.getWeight();
        packets.put(pp, packet);
      }
    }
  }

  // ............................................................................

  private MyPacketPos checkPutPacket(final Pallet pallet, final Packet packet, final int x, final int y, final boolean rotated)
      throws PacketAlreadyUsedException, WrongTruckException, WeightExceededException, PalletExtendsViolatedException,
      HeightExceededException {
    final PacketInfo packetInfo = packetInfos.get(Integer.valueOf(packet.getId()));
    if (packetInfo == null) {
      throw new IllegalArgumentException("NOT FOUND: " + packet + " => pallet.id=" + pallet.getId() + "\n");
    }
    if (packetInfo.pallet != null) {
      throw new PacketAlreadyUsedException(pallet, packet, packetInfo.pallet);
    }
    if (pallet.getTruckId() != packet.getTruckId()) {
      throw new WrongTruckException(pallet, packet, x, y, rotated);
    }
    if ((pallet.getCurrentWeight() + packet.getWeight()) > pallet.getType().getMaxWeight()) {
      throw new WeightExceededException(pallet, packet, x, y, rotated);
    }
    final boolean isLengthwise = !rotated;
    final int pall = isLengthwise ? packet.getLength() : packet.getWidth();
    final int palw = isLengthwise ? packet.getWidth() : packet.getLength();
    if ((x < 0) || (y < 0) || ((x + pall) > pallet.getType().getLength()) || ((y + palw) > pallet.getType().getWidth())) {
      throw new PalletExtendsViolatedException(pallet, packet, x, y, rotated);
    }
    final int layer = findInductLayer(pallet, x, y, pall, palw);
    if (layer < 0) {
      throw new HeightExceededException(pallet, packet, x, y, rotated);
    }
    final MyPacketPos mpp = new MyPacketPos(layer, x, y, rotated);
    return mpp;
  }

  private int findInductLayer(final Pallet pallet, final int x, final int y, final int pall, final int palw) {
    int layer = -1;
    for (int l = pallet.getType().getMaxHeight() - 1; l >= 0; l--) {
      if (!fits(pallet.getLayer(l), x, y, pall, palw)) {
        break;
      }
      layer = l;
    }
    return layer;
  }

  private boolean fits(final Pallet.Layer layer, final int x, final int y, final int pall, final int palw) {
    for (final Map.Entry<Pallet.PacketPos, Packet> epp : layer.getPackets().entrySet()) {
      if (overlap(epp, x, y, pall, palw)) {
        return false;
      }
    }
    return true;
  }

  private boolean overlap(final Entry<Pallet.PacketPos, Packet> eppp, final int x, final int y, final int pall,
      final int palw) {
    final Pallet.PacketPos epp = eppp.getKey();
    final Packet ep = eppp.getValue();
    final int ex1 = epp.getX();
    final int ex2 = ex1 + (epp.isRotated() ? ep.getWidth() : ep.getLength());
    final int ey1 = epp.getY();
    final int ey2 = ey1 + (epp.isRotated() ? ep.getLength() : ep.getWidth());
    final int px1 = x;
    final int px2 = px1 + pall;
    final int py1 = y;
    final int py2 = py1 + palw;

    final boolean ovx = overlap(ex1, ex2, px1, px2);
    final boolean ovy = overlap(ey1, ey2, py1, py2);
    return (ovx && ovy);
  }

  private boolean overlap(final int v11, final int v12, final int v21, final int v22) {
    return ((v12 > v21) && (v11 < v22));
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  private void doPutPacket(final PalletInternal pallet, final MyPacketPos pp, final Packet packet) {
    pallet.getLayer(pp.layer).put(pp, packet);
    packetInfos.get(Integer.valueOf(packet.getId())).pallet = pallet;
    operations.add(new WarehouseInternal.PutPacket(pallet, packet, pp));
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  static abstract class WarehouseOperation {
    private final String toResultString;

    private WarehouseOperation(final Object... args) {
      final StringBuilder sb = new StringBuilder();
      sb.append(getClass().getSimpleName()).append(";");
      for (final Object arg : args) {
        sb.append(arg).append(";");
      }
      toResultString = sb.toString();
    }

    public final String toResultString() {
      return toResultString;
    }

    @Override
    public String toString() {
      return toResultString();
    }
  }

  // ----------------------------------------------------------------------------

  private static class PreparePallet extends WarehouseOperation {
    @SuppressWarnings("boxing")
    private PreparePallet(final PalletInternal pallet) {
      super(pallet.getId(), pallet.getTruckId(), pallet.getType().getId());
    }
  }

  private static class PutPacket extends WarehouseOperation {
    @SuppressWarnings("boxing")
    private PutPacket(final PalletInternal pallet, final Packet packet, final MyPacketPos pp) {
      super(pallet.getId(), packet.getId(), pp.getX(), pp.getY(), pp.isRotated());
    }
  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static class MyPacketPos extends Pallet.PacketPos {
    private static final long serialVersionUID = 1L;

    private final int layer;

    public MyPacketPos(final int layer, final int x, final int y, final boolean rotated) {
      super(x, y, rotated);
      this.layer = layer;
    }

    @Override
    public String toString() {
      return "MyPacketPos[layer=" + layer + ", " + super.toString() + "]";
    }
  }

  // ............................................................................

  private static class PacketInfo {
    private volatile Pallet pallet;

    @SuppressWarnings("unused")
    private PacketInfo(final Packet packet) {
    }
  }

  // ............................................................................

  private long area(final Pallet pallet) {
    final PalletType pt = pallet.getType();
    return pt.getLength() * pt.getWidth() * 1;
  }

  private long volumeUsed(final Pallet pallet) {
    final PalletType pt = pallet.getType();
    return pt.getLength() * pt.getWidth() * usedLayers(pallet);
  }

  private int usedLayers(final Pallet pallet) {
    int usedLayers = 0;
    final PalletType pt = pallet.getType();
    for (int l = 0; l < pt.getMaxHeight(); l++) {
      final Pallet.Layer layer = pallet.getLayer(l);
      if (!layer.getPackets().isEmpty()) {
        usedLayers++;
      }
    }
    return usedLayers;
  }

  // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

  private static class WarehouseInfoSnapshot implements WarehouseInfo, Serializable {
    private static final long serialVersionUID = 1L;

    private final int unfinishedPackets;
    private final long palletsCount;
    private final long palletsArea;
    private final long palletsVolumeUsed;
    private final long unfinishedPacketsCost;
    private final long palletsAreaCost;
    private final long palletsVolumeUsedCost;
    private final long totalCost;

    private WarehouseInfoSnapshot(final WarehouseInternal warehouseInternal) {
      long a = 0;
      long vu = 0;
      for (final Pallet pallet : warehouseInternal.pallets) {
        a += warehouseInternal.area(pallet);
        vu += warehouseInternal.volumeUsed(pallet);
      }
      int up = 0;
      for (final PacketInfo pi : warehouseInternal.packetInfos.values()) {
        if (pi.pallet == null) {
          up++;
        }
      }
      unfinishedPackets = up;
      palletsCount = warehouseInternal.pallets.size();
      palletsArea = a;
      palletsVolumeUsed = vu;
      unfinishedPacketsCost = (long) ((unfinishedPackets * warehouseInternal.getCostFactors().getUnfinishedPacketCosts()) //
          + (unfinishedPackets > 0 ? warehouseInternal.getCostFactors().getUnfinishedPacketsPenalty() : 0));
      palletsAreaCost = (long) (palletsArea * warehouseInternal.getCostFactors().getPalletAreaCosts());
      palletsVolumeUsedCost = (long) (palletsVolumeUsed * warehouseInternal.getCostFactors().getPalletVolumeUsedCosts());
      totalCost = palletsAreaCost + palletsVolumeUsedCost + unfinishedPacketsCost;
    }

    @Override
    public synchronized long getTotalCost() {
      return totalCost;
    }

    @Override
    public synchronized long getPalletsAreaCost() {
      return palletsAreaCost;
    }

    @Override
    public synchronized long getPalletsVolumeUsedCost() {
      return palletsVolumeUsedCost;
    }

    @Override
    public synchronized long getUnfinishedPacketsCost() {
      return unfinishedPacketsCost;
    }

    @Override
    public synchronized long getPalletCount() {
      return palletsCount;
    }

    @Override
    public synchronized long getPalletsArea() {
      return palletsArea;
    }

    @Override
    public synchronized long getPalletsVolumeUsed() {
      return palletsVolumeUsed;
    }

    @Override
    public synchronized int getUnfinishedPacketCount() {
      return unfinishedPackets;
    }
  }

  // ............................................................................

  public static class CostFactors implements WarehouseCostFactors {
    @Override
    public double getUnfinishedPacketsPenalty() {
      return 5_000_000.0;
    }

    @Override
    public double getUnfinishedPacketCosts() {
      return 100.0;
    }

    @Override
    public double getPalletAreaCosts() {
      return 1.0;
    }

    @Override
    public double getPalletVolumeUsedCosts() {
      return 0.1;
    }
  }

  // ----------------------------------------------------------------------------
}
