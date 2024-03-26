/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.swing.filechooser.FileFilter;
import org.opentcs.access.to.model.PlantModelCreationTO;

/**
 * Interface to read a file containing a {@link PlantModelCreationTO}.
 */
public interface ModelFileReader {

  /**
   * Deserializes the model contained in the given file.
   *
   * @param file The {@link File} containing the model.
   * @return The deserialized {@link PlantModelCreationTO} or {@link Optional#EMPTY} if
   * deserialzation canceled.
   * @throws java.io.IOException If an exception occured while reading
   * the file.
   */
  Optional<PlantModelCreationTO> deserialize(File file)
      throws IOException;

  /**
   * Returns the filter that declares which files are supported with this reader.
   *
   * @return The filter that declares which files are supported with this reader
   */
  @Nonnull
  FileFilter getDialogFileFilter();
}
