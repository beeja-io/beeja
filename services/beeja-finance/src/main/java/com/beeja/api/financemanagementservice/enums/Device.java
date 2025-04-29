package com.beeja.api.financemanagementservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
  KEYBOARD ("Keyboard"),
  CCTV("cctv"),
  UPS("ups"),
  ACCESSORIES("Accessories");



  private final String displayName;

  Device(String displayName) {
    this.displayName = displayName;
  }
  @JsonValue
  public String getDisplayName() {
    return displayName;
  }

  @JsonCreator
  public static Device fromValue(String value) {
    for (Device device : Device.values()) {
      if (device.displayName.equalsIgnoreCase(value)) {
        return device;
      }
    }
    throw new IllegalArgumentException("Unknown device: " + value);
  }
}
