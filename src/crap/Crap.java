package crap;

import java.util.List;

import gui.ResourceFinder;
import gui.ResourceFinder.ResourceEntry;

public class Crap {

  public static void main(String[] args) {
    List<ResourceEntry> hipTex = ResourceFinder.INST.findTextures("hipso");
  }

}
