package com.beeja.api.financemanagementservice.enums;

import lombok.Getter;

@Getter
public enum Device {
  LAPTOP("Laptop"),
  MOBILE("Mobile"),
  DESKTOP("Desktop"),
  PRINTER("Printer"),
  MUSIC_SYSTEM("Music System"),
  TABLET("Tablet"),
  SERVER("Server"),
  SMARTPHONE("Smartphone"),
  ROUTER("Router"),
  KEYBOARD("Keyboard"),
  CCTV("cctv"),
  UPS("ups"),
  ACCESSORIES("Accessories");

  private final String displayName;

  Device(String displayName) {
    this.displayName = displayName;
  }
}
