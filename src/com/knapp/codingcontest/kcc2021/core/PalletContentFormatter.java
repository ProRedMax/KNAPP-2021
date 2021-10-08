package com.knapp.codingcontest.kcc2021.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.knapp.codingcontest.kcc2021.data.Packet;
import com.knapp.codingcontest.kcc2021.data.Pallet;
import com.knapp.codingcontest.kcc2021.data.Pallet.Layer;
import com.knapp.codingcontest.kcc2021.data.Pallet.PacketPos;
import com.knapp.codingcontest.kcc2021.data.PalletType;

public class PalletContentFormatter
{
  private static final int MAX_PALLET_LENGTH = 12;


  final static int U = 1;
  final static int D = 2;
  final static int L = 4;
  final static int R = 8;

  final static char[] chr = new char[16];

  /*
   * ┌┬┐ ├┼┤ ├┴┤ └─┘
   */

  static
  {
    chr[0] = ' ';

    chr[U | D] = '│';
    chr[L | R] = '─';

    chr[L | U] = '┘';
    chr[U | R] = '└';
    chr[R | D] = '┌';
    chr[D | L] = '┐';

    chr[L | U | R] = '┴';
    chr[U | R | D] = '├';
    chr[R | D | L] = '┬';
    chr[D | L | U] = '┤';

    chr[U | D | L | R] = '┼';
  }

  private static void addPacket(byte[][] grid, int x, int y, int length, int width)
  {
    grid[x][y] |= R | D;
    grid[x + length][y] |= L | D;
    grid[x][y + width] |= R | U;
    grid[x + length][y + width] |= L | U;

    for (int mX = x + 1; mX < x + length; mX++)
    {
      grid[mX][y] |= L | R;
      grid[mX][y + width] |= L | R;
    }

    for (int mY = y + 1; mY < y + width; mY++)
    {
      grid[x][mY] |= U | D;
      grid[x + length][mY] |= U | D;
    }
  }


  private static byte[][] prepareGrid(int length, int width, Layer layer)
  {
    final byte[][] grid = new byte[length + 1][width + 1];
    grid[0][0] = R | D;
    grid[length][0] = L | D;
    grid[0][width] = R | U;
    grid[length][width] = L | U;

    for (Map.Entry<PacketPos, Packet> entry : layer.getPackets().entrySet())
    {
      PacketPos packetPos = entry.getKey();
      Packet packet = entry.getValue();

      int packetLength = packetPos.isRotated() ? packet.getWidth() : packet.getLength();
      int packetWidth = packetPos.isRotated() ? packet.getLength() : packet.getWidth();
      
      addPacket(grid, packetPos.getX(), packetPos.getY(), packetLength, packetWidth);
    }

    return grid;
  }

  private static String LF = System.getProperty("line.separator");

  private static String padRight(String s, int n)
  {
    return String.format("%-" + n + "s", s);
  }

  public static String formatContent(Pallet pallet)
  {
    StringBuilder s = new StringBuilder();

    List<byte[][]> grids = new ArrayList<byte[][]>();
    PalletType palletType = pallet.getType();

    for (int level = 0; level < palletType.getMaxHeight(); level++)
    {
      grids.add(prepareGrid(palletType.getLength(), palletType.getWidth(), pallet.getLayer(level)));
      s.append(padRight("Layer " + level, MAX_PALLET_LENGTH + 1));
    }
    s.append(LF);

    for (int y = 0; y < palletType.getWidth() + 1; y++)
    {
      for (byte[][] grid : grids)
      {
        for (int x = 0; x < MAX_PALLET_LENGTH + 1; x++)
        {
          if(x<grid.length)
          {
            s.append(chr[grid[x][y]]);
          }
          else
          {
            s.append(" ");
          }
        }
      }
      s.append(LF);
    }
    return s.toString();
  }
}
