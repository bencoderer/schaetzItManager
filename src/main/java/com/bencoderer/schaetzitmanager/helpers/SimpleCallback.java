package com.bencoderer.schaetzitmanager.helpers;

public interface SimpleCallback
{ 
  void onSuccess(Object... objects);
  void onError(Throwable err);
}
