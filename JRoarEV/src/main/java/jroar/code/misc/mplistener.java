package jroar.code.misc;

import jroar.code.com.jcraft.jroar.MountPointListener;

public class mplistener implements MountPointListener{
  public void mount(String mountpoint){
    System.out.println("mount: "+mountpoint);
  }
  public void unmount(String mountpoint){
    System.out.println("unmount: "+mountpoint);
  }
}
