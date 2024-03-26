/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.util;

import javax.swing.ImageIcon;

/**
 * This utility class declares the main image directory.
 * It also provides utility methods to get the url to files in the directory.
 */
public final class ImageDirectory {

  /**
   * The directory where GUI images and icons are stored.
   */
  public static final String DIR = "/org/opentcs/guing/res/symbols";

  /**
   * Prevents instantiation.
   */
  private ImageDirectory() {
  }

  /**
   * Returns an ImageIcon from a relative path inside the image directory.
   *
   * @param relPath path to file inside the image directory.
   * @return the new ImageIcon
   */
  public static ImageIcon getImageIcon(String relPath) {
    return new ImageIcon(ImageDirectory.class.getResource(DIR + relPath));
  }
}
