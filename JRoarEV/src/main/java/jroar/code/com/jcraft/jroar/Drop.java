/* -*-mode:java; c-basic-offset:2; -*- */
/* JRoar -- pure Java streaming server for Ogg
 *
 * Copyright (C) 2001,2002 ymnk, JCraft,Inc.
 *
 * Written by: 2001,2002 ymnk<ymnk@jcraft.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package jroar.code.com.jcraft.jroar;

import java.util.*;

public class Drop {
    // Evolución: Drop deja de ser una interfaz web (heredar de Page)
    // Esta interfaz web sirve para borrar un mountpoint

    static public boolean deleteMountPoint(String mpoint) {
        // Se reutiliza el método kick
        if (mpoint!=null && Source.getSource(mpoint) != null) {
            Source.getSource(mpoint).drop();
            return true;
        }
        return false;
    }
    // Evolución: método para borrar todos los puntos de montura a la vez
    static public void deleteAll() {
        // Se reutiliza el método kick
        Enumeration keys = Source.sources.keys();
        while (keys.hasMoreElements()) {
            String mountpoint = ((String) (keys.nextElement()));
            Source.getSource(mountpoint).drop();
        }
    }
}
