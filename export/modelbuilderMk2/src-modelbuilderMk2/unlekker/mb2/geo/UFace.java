/*
 * modelbuilderMk2
 */
package unlekker.mb2.geo;

import java.util.Arrays;

import unlekker.mb2.util.UMB;

/**
 * TODO  
 * - UFace.equals(Object o)
 * 
 * @author marius
 *
 */
public class UFace extends UMB  {
  public static int globalID=0;

  public UGeo parent;
  public int ID;  
  public int vID[];
  public UVertex v[];
  
  public int col;
  public UVertex normal,centroid;
  
  public UFace() {
    ID=globalID++;
    vID=new int[] {-1,-1,-1};
    col=Integer.MAX_VALUE;
  }

  public UFace(UFace v) {
    this();
    set(v);
  }

  public UFace(UGeo model, UVertex v1, UVertex v2, UVertex v3) {
    this();
    if(model!=null) parent=model;
    
    set(v1,v2,v3);
    
//    log("F "+ID+" "+vID[0]+" "+vID[1]+" "+vID[2]);
  }

  public UFace(UGeo model, int[] ID) {
    this();
    parent=model;
    vID=new int[] {ID[0],ID[1],ID[2]};
  }

  public UFace set(UVertex v1, UVertex v2, UVertex v3) {
    if(parent!=null) {
      vID[0]=parent.addVertex(v1);
      vID[1]=parent.addVertex(v2);
      vID[2]=parent.addVertex(v3);
    }
    else {
      if(v==null) v=new UVertex[3];
      v[0]=v1;
      v[1]=v2;
      v[2]=v3;
    }
    
   
    return this;
  }

  public UFace drawNormal(float len) {
    return drawNormal(len,true);
  }

  public UFace drawNormal(float len,boolean centerOnly) {
    if(checkGraphicsSet()) {  
      UVertex n=normal();
      
      if(centerOnly) {
        ppush().ptranslate(centroid());
        g.line(0, 0, 0, n.x*len,n.y*len,n.z*len);
        ppop();
      }
      else {
        getV();
        for(UVertex vert:v) {
          ppush().ptranslate(vert);
          g.line(0, 0, 0, n.x*len,n.y*len,n.z*len);
          ppop();
        }

      }
    }

    return this;
  }

  public UFace draw() {
    if(checkGraphicsSet()) {
      if(col!=Integer.MAX_VALUE) g.fill(col);
      if(v==null) getV();
      g.beginShape(TRIANGLE);
      pvertex(v);
      g.endShape();
    }
    
    return this;
  }

  public UVertex[] getV(boolean force) {
    if(force) v=null;
    return getV();
  }

  public UVertex[] getV() {
    if(parent==null || v!=null) return v;
    
    if(v==null) v=new UVertex[3];
 
    int id=0;    
    for(int vid:vID) v[id++]=parent.getVertex(vid);
    
    return v;
  }
  
  public UFace copy() {
    return new UFace(this);
  }
    
  public UFace setParent(UGeo geo) {
    parent=geo;
    return this;
  }

  public UFace set(UFace v) {
    vID=new int[] {v.vID[0],v.vID[1],v.vID[2]};
    col=v.col;
    parent=v.parent;
    
    if(v.normal!=null) {
      if(normal!=null) normal.set(v.normal.x,v.normal.y,v.normal.x);
    }
    
   return this;
  }

  public UFace setColor(int a) {
    col=a;
    return this;
  }

  public UFace setColor(float a,float b,float c) {
    return setColor(color(a,b,c));
  }

  public UVertex centroid() {
    if(centroid==null) {
      getV();
      centroid=new UVertex().
          add(v[0]).add(v[1]).add(v[2]);
      log(UVertex.str(v)+" -> "+centroid.str());
      centroid.div(3);
    }
    
    return centroid;
  }

  public UFace reverse() {
    if(parent!=null) {
      vID=new int[] {vID[0],vID[2],vID[1]};
      v=null;
    }
    else {
      UVertex v2=v[2];
      v[2]=v[1];
      v[1]=v2;
    }
    
    if(normal!=null) {
      normal=null;
      normal();
    }
    return this;
  }
  
  public UVertex normal() {
    if(normal!=null) return normal;
        
    getV();
    
    normal=UVertex.cross(
        UVertex.delta(v[1],v[0]), 
        UVertex.delta(v[2],v[0]));
    normal.norm();
    return normal;
  }
}
