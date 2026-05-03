package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "spatial_ref_sys")
public class SpatialRefSys {
    @Id
    @Column(name = "srid")
    private Integer srid;

    @Column(name = "auth_name", length = 256)
    private String authName;

    @Column(name = "auth_srid")
    private Integer authSrid;

    @Column(name = "srtext", length = 2048)
    private String srText;

    @Column(name = "proj4text", length = 2048)
    private String proj4Text;

    public SpatialRefSys() {
    }

    public SpatialRefSys(Integer srid, String authName, Integer authSrid, String srText, String proj4Text) {
        this.srid = srid;
        this.authName = authName;
        this.authSrid = authSrid;
        this.srText = srText;
        this.proj4Text = proj4Text;
    }

    public Integer getSrid() {
        return srid;
    }

    public void setSrid(Integer srid) {
        this.srid = srid;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public Integer getAuthSrid() {
        return authSrid;
    }

    public void setAuthSrid(Integer authSrid) {
        this.authSrid = authSrid;
    }

    public String getSrText() {
        return srText;
    }

    public void setSrText(String srText) {
        this.srText = srText;
    }

    public String getProj4Text() {
        return proj4Text;
    }

    public void setProj4Text(String proj4Text) {
        this.proj4Text = proj4Text;
    }
}