/*
  Interface for the RulDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package com.cbms.source.local;

public interface RulDAO {
    double getLatestRUL(int assetID);
}
