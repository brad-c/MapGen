package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceFinder {

  public static ResourceFinder INST = new ResourceFinder();
  
  public ResourceFinder() {
  }
  
  public List<ResourceEntry> findTextures(String prefix) {
    return findEntries("textures", prefix, ".png");
  }
  
  public List<ResourceEntry> findEntries(String path, String prefix, String postFix) {
    List<ResourceEntry> result = new ArrayList<>();
    
    try {
      List<String> files = getResourceFiles(path);
      for(String file : files) {
        if(file.endsWith(postFix) && file.startsWith(prefix)) {
          result.add(new ResourceEntry(path, file, prefix, postFix));
        }
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return result;
    
  }
  
  private List<String> getResourceFiles( String path ) throws IOException {
    List<String> filenames = new ArrayList<>();

    try(
      InputStream in = getResourceAsStream( path );
      BufferedReader br = new BufferedReader( new InputStreamReader( in ) ) ) {
      String resource;

      while( (resource = br.readLine()) != null ) {
        filenames.add( resource );
      }
    }

    return filenames;
  }

  private InputStream getResourceAsStream( String resource ) {
    final InputStream in
      = getContextClassLoader().getResourceAsStream( resource );

    return in == null ? getClass().getResourceAsStream( resource ) : in;
  }

  private ClassLoader getContextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  public static class ResourceEntry {
    
    public String resource;
    public String prefix;
    public String postFix;
    public String name;
    public String displayName;
    
    public ResourceEntry(String path, String name, String prefix, String postFix) {
      this.resource = path + "/" + name;
      this.name = name;
      this.prefix = prefix;
      this.postFix = postFix;
      displayName = name.substring(prefix.length(), name.length() - postFix.length());
    }

    @Override
    public String toString() {
      return displayName;
    }
    
    
    
  }
  
}
