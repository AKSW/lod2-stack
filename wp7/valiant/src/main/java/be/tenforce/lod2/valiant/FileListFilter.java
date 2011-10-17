package be.tenforce.lod2.valiant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

class FileListFilter implements FilenameFilter {
  private String extension;
  public FileListFilter(String extension) {
    this.extension = extension;
  }
  public boolean accept(File directory, String filename) {
    boolean fileOK = true;
    if (extension != null) {
      fileOK = filename.endsWith('.' + extension);
    }
    return fileOK;
  }
}
