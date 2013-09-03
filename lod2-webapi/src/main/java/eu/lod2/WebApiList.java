package eu.lod2;

import java.util.List;
import java.util.ArrayList;

public class WebApiList{

  private ArrayList<String> resultList;
  private Integer   start;
  private Integer   listSize;
  private Boolean   more;
  private Integer   totalAmount;

  public ArrayList<String> getResultList() {
    return resultList;
  }

  public void setResultList(List<String> list) {
    this.resultList = (ArrayList) list;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public Integer getListSize() {
    return listSize;
  }

  public void setListSize(Integer listSize) {
    this.listSize = listSize;
  }

  public Integer getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Integer totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Boolean getMore() {
    return more;
  }

  public void setMore(Boolean more) {
    this.more = more;
  }

}
