package com.yss.main.operdeal.datainterface.pretfun;

//Programmer: Dranson
import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

public class CalcBean {
    //1、A股印花税率，过户费率，经手费率，证管费率
    private double dAgYHS_SH = 0, dAgGHF_SH = 0, dAgJSF_SH = 0, dAgZGF_SH = 0, dAgYHS_SZ = 0, dAgJSF_SZ = 0, dAgZGF_SZ = 0, dETFGhf_SH = 0, dETFGhf_SZ = 0;
    private double dAgYHS_SH_Min = 0, dAgGHF_SH_Min = 0, dAgJSF_SH_Min = 0, dAgZGF_SH_Min = 0, dAgYHS_SZ_Min = 0, dAgJSF_SZ_Min = 0, dAgZGF_SZ_Min = 0;

    //2、国债现券经手费率，证管费率
    private double dGzxqJSF_SH = 0, dGzxqZGF_SH = 0, dGzxqJSF_SZ = 0, dGzxqZGF_SZ = 0, dGzxqJSF_SH_Min = 0, dGzxqZGF_SH_Min = 0, dGzxqJSF_SZ_Min = 0, dGzxqZGF_SZ_Min = 0,
    GdGzxqJSF_SH = 0, GdGzxqJSF_SH_Min = 0, GdGzxqZGF_SH = 0, GdGzxqZGF_SH_Min = 0;

    //3、企业债券经手费率，证管费率
    private double dQyzqJSF_SH = 0, dQyzqZGF_SH = 0, dQyzqJSF_SZ = 0, dQyzqZGF_SZ = 0, dQyzqJSF_SH_Min = 0, dQyzqZGF_SH_Min = 0, dQyzqJSF_SZ_Min = 0, dQyzqZGF_SZ_Min = 0,
    GdQyzqJSF_SH = 0, GdQyzqJSF_SH_Min = 0, GdQyzqZGF_SH = 0, GdQyzqZGF_SH_Min = 0;

    //4、可转换债券经手费率，证管费率
    private double dKzzJSF_SH = 0, dKzzZGF_SH = 0, dKzzJSF_SZ = 0, dKzzZGF_SZ = 0, dKzzJSF_SH_Min = 0, dKzzZGF_SH_Min = 0, dKzzJSF_SZ_Min = 0, dKzzZGF_SZ_Min = 0;

    //5、证券投资基金印花税率，过户费率，经手费率，证管费率
    private double dJjYHS_SH = 0, dJjGHF_SH = 0, dJjJSF_SH = 0, dJjZGF_SH = 0, dJjYHS_SZ = 0, dJjJSF_SZ = 0, dJjZGF_SZ = 0;
    private double dJjYHS_SH_Min = 0, dJjGHF_SH_Min = 0, dJjJSF_SH_Min = 0, dJjZGF_SH_Min = 0, dJjYHS_SZ_Min = 0, dJjJSF_SZ_Min = 0, dJjZGF_SZ_Min = 0;

    //6、新股经手费率，手续费率
    private double dXgJSF_SH = 0, dXgSXF_SH = 0, dXgSXF_SZ = 0, dXgJSF_SZ = 0;
    //7、配股经手费率，手续费率
    private double dPgJSF_SH = 0, dPgSXF_SH = 0, dPgSXF_SZ = 0, dPgJSF_SZ = 0;
    //8、配债经手费率，手续费率
    private double dPzJSF_SH = 0, dPzSXF_SH = 0, dPzSXF_SZ = 0, dPzJSF_SZ = 0;

    //9.权证
    private double dQzJSF_SH = 0, dQzZGF_SH = 0, dQzSXF_SH = 0, dQzJSF_SZ = 0, dQzZGF_SZ = 0, dQzSXF_SZ = 0;

    //10.资产证券化产品－ZCZQ；
    private double dZcZqJSF_SH = 0, dZcZqZGF_SH = 0, dZcZqSXF_SH = 0, dZcZqJSF_SZ = 0, dZcZqZGF_SZ = 0, dZcZqSXF_SZ = 0;

    private DataCache cache = null;
    public static String strTableMx =
        "(fdate,findate,fjyfs,zqdm,fzqdm, fszsh, fgddm, fjyxwh, fbs, fcjsl, fcjjg, fcjje, fyhs, fjsf, fghf, fzgf, fyj, ffxj ,fqtf,fgzlx, fhggain, fzqbz, fywbz,fcjbh)";
    public static String strTableHz =
        "(fdate,findate,fjyfs,zqdm,fzqdm, fszsh, fgddm, fjyxwh, fbs, fcjsl, fcjje, fyhs, fjsf, fghf, fzgf, fyj, ffxj , fqtf ,fgzlx, fhggain, fzqbz, fywbz,fcjbh)";
    public static String strTableQs = "(fdate,findate,fjyfs,zqdm,fzqdm, fszsh, fjyxwh, fbje, fsje, fbsl, fssl, fbyj, fsyj, fbjsf, fsjsf, fbyhs, fsyhs, fbzgf, fszgf, fbghf, fsghf, fbfxj , fsfxj , fbqtf,fsqtf,fbgzlx, fsgzlx, fhggain, fbsfje, fsssje, fzqbz, fywbz, fqsbz)";
    public static String strTableXwTj = "(fdate, fqsdm, fqsxw, fzqbz, fywbz, fszsh, fbsl, fssl, fbje, fsje, fbyj, fsyj, fbyhs, fsyhs, fhgje, ffxj)";
    private YssPub pub = null;
    private java.util.Date date = null;
    public CalcBean() {}

    public CalcBean(YssPub pub, java.util.Date date) throws YssException {
        this.pub = pub;
        cache = new DataCache();
        setDate(date);
        init(date);
    }

    public YssPub getYssPub() {
        return pub;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    private void init(java.util.Date date) throws YssException {
        ResultSet rs = null;
        DbBase dbl = pub.getDbLink();
        try {
            String strSql = "select max(fstartdate),fszsh,ffvlb,fzqlb,flv,fje,fjjdm,fxwgd from CsJylv where FFvlb<>'HGF' and fstartdate<="
                + dbl.sqlDate(date) + " and fsh=1 and FJJdm = '0' group by fszsh,ffvlb,fzqlb,flv,fje,fjjdm,fxwgd order by fszsh";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FSzsh").trim().equalsIgnoreCase("H")) { //上海部分
                    if (rs.getString("FFvlb").trim().equalsIgnoreCase("YHS")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgYHS_SH(rs.getDouble("FLv"));
                            this.setAgYHS_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjYHS_SH(rs.getDouble("FLv"));
                            this.setJjYHS_SH_Min(rs.getDouble("FJe"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("GHF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgGHF_SH(rs.getDouble("FLv"));
                            this.setAgGHF_SH_Min(rs.getDouble("FJe"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ETFGP")) {
                            this.setEtfGhf_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjGHF_SH(rs.getDouble("FLv"));
                            this.setJjGHF_SH_Min(rs.getDouble("FJe"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("JSF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgJSF_SH(rs.getDouble("FLv"));
                            this.setAgJSF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("GZXQ")) {
                            this.setGzxqJSF_SH(rs.getDouble("FLv"));
                            this.setGzxqJSF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QYZQ")) {
                            this.setQyzqJSF_SH(rs.getDouble("FLv"));
                            this.setQyzqJSF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("KZZ")) {
                            this.setKzzJSF_SH(rs.getDouble("FLv"));
                            this.setKzzJSF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjJSF_SH(rs.getDouble("FLv"));
                            this.setJjJSF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("XG")) {
                            this.setXgJSF_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("PG")) {
                            this.setPgJSF_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("PZ")) {
                            this.setPzJSF_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QZ")) {
                            this.setQzJSF_SH(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ZCZQ")) {
                            this.setZcZqJSF_SH(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GDGZ")) {
                            this.setGdGzxqJSF_SH(rs.getDouble("FLv"));
                            this.setGdGzxqJSF_SH_Min(rs.getDouble("FJe"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GDQZ")) {
                            this.setGdQyzqJSF_SH(rs.getDouble("FLv"));
                            this.setGdQyzqJSF_SH_Min(rs.getDouble("FJe"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("ZGF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgZGF_SH(rs.getDouble("FLv"));
                            this.setAgZGF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("GZXQ")) {
                            this.setGzxqZGF_SH(rs.getDouble("FLv"));
                            this.setGzxqZGF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QYZQ")) {
                            this.setQyzqZGF_SH(rs.getDouble("FLv"));
                            this.setQyzqZGF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("KZZ")) {
                            this.setKzzZGF_SH(rs.getDouble("FLv"));
                            this.setKzzZGF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjZGF_SH(rs.getDouble("FLv"));
                            this.setJjZGF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QZ")) {
                            this.setQzZGF_SH(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ZCZQ")) {
                            this.setZcZqZGF_SH(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GDGZ")) {
                            this.setGdGzxqZGF_SH(rs.getDouble("FLv"));
                            this.setGdGzxqZGF_SH_Min(rs.getDouble("FJe"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GDQZ")) {
                            this.setGdQyzqZGF_SH(rs.getDouble("FLv"));
                            this.setGdQyzqZGF_SH_Min(rs.getDouble("FJe"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("SXF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("XG")) {
                            this.setXgSXF_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("PG")) {
                            this.setPgSXF_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("PZ")) {
                            this.setPzSXF_SH(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QZ")) {
                            this.setQzSXF_SH(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ZCZQ")) {
                            this.setZcZqSXF_SH(rs.getDouble("FLv"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("FXJ")) {
                        cache.setDouble(rs.getString("fjjdm") + rs.getString("fxwgd").toUpperCase() + rs.getString("ffvlb") + "Lv_" + rs.getString("FSZSH"), rs.getDouble("FLv"));

                    }
                } else { //深圳部分
                    if (rs.getString("FFvlb").trim().equalsIgnoreCase("YHS")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgYHS_SZ(rs.getDouble("FLv"));
                            this.setAgYHS_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjYHS_SZ(rs.getDouble("FLv"));
                            this.setJjYHS_SH_Min(rs.getDouble("FJe"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("JSF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgJSF_SZ(rs.getDouble("FLv"));
                            this.setAgJSF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("GZXQ")) {
                            this.setGzxqJSF_SZ(rs.getDouble("FLv"));
                            this.setGzxqJSF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QYZQ")) {
                            this.setQyzqJSF_SZ(rs.getDouble("FLv"));
                            this.setQyzqJSF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("KZZ")) {
                            this.setKzzJSF_SZ(rs.getDouble("FLv"));
                            this.setKzzJSF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjJSF_SZ(rs.getDouble("FLv"));
                            this.setJjJSF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QZ")) {
                            this.setQzJSF_SZ(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ZCZQ")) {
                            this.setZcZqJSF_SZ(rs.getDouble("FLv"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("ZGF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("GP")) {
                            this.setAgZGF_SZ(rs.getDouble("FLv"));
                            this.setAgZGF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("JJ")) {
                            this.setJjZGF_SZ(rs.getDouble("FLv"));
                            this.setJjZGF_SH_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("GZXQ")) {
                            this.setGzxqZGF_SZ(rs.getDouble("FLv"));
                            this.setGzxqZGF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QYZQ")) {
                            this.setQyzqZGF_SZ(rs.getDouble("FLv"));
                            this.setQyzqZGF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("KZZ")) {
                            this.setKzzZGF_SZ(rs.getDouble("FLv"));
                            this.setKzzZGF_SZ_Min(rs.getDouble("FJe"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QZ")) {
                            this.setQzZGF_SZ(rs.getDouble("FLv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ZCZQ")) {
                            this.setZcZqZGF_SZ(rs.getDouble("FLv"));
                        }

                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("SXF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("XG")) {
                            this.setXgSXF_SZ(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("PG")) {
                            this.setPgSXF_SZ(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("PZ")) {
                            this.setPzSXF_SZ(rs.getDouble("FLv"));
                        } else if (rs.getString("FZqlb").trim().equalsIgnoreCase("QZ")) {
                            this.setQzSXF_SZ(rs.getDouble("Flv"));
                        }
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ZCZQ")) {
                            this.setZcZqSXF_SZ(rs.getDouble("FLv"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("GHF")) {
                        if (rs.getString("FZqlb").trim().equalsIgnoreCase("ETFGP")) {
                            this.setEtfGhf_SZ(rs.getDouble("FLv"));
                        }
                    } else if (rs.getString("FFvlb").trim().equalsIgnoreCase("FXJ")) {
                        cache.setDouble(rs.getString("fjjdm") + rs.getString("fzqlb") + rs.getString("fxwgd").toUpperCase() + rs.getString("ffvlb") + "Lv_" + rs.getString("FSZSH"),
                                        rs.getDouble("FLv"));

                    }
                }
            }
            rs.getStatement().close();
            rs = null;
        } catch (Exception ex) {
            throw new YssException("获取交易所费率出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getAgYHS_SH() {
        return dAgYHS_SH;
    }

    public double getAgGHF_SH() {
        return dAgGHF_SH;
    }

    public double getAgJSF_SH() {
        return dAgJSF_SH;
    }

    public double getAgZGF_SH() {
        return dAgZGF_SH;
    }

    public double getAgYHS_SZ() {
        return dAgYHS_SZ;
    }

    public double getAgJSF_SZ() {
        return dAgJSF_SZ;
    }

    public double getAgZGF_SZ() {
        return dAgZGF_SZ;
    }

    public double getGzxqJSF_SH() {
        return dGzxqJSF_SH;
    }

    public double getGzxqZGF_SH() {
        return dGzxqZGF_SH;
    }

    public double getGzxqJSF_SZ() {
        return dGzxqJSF_SZ;
    }

    public double getGzxqZGF_SZ() {
        return dGzxqZGF_SZ;
    }

    public double getQyzqJSF_SH() {
        return dQyzqJSF_SH;
    }

    public double getQyzqZGF_SH() {
        return dQyzqZGF_SH;
    }

    public double getQyzqJSF_SZ() {
        return dQyzqJSF_SZ;
    }

    public double getQyzqZGF_SZ() {
        return dQyzqZGF_SZ;
    }

    public double getKzzJSF_SH() {
        return dKzzJSF_SH;
    }

    public double getKzzZGF_SH() {
        return dKzzZGF_SH;
    }

    public double getKzzJSF_SZ() {
        return dKzzJSF_SZ;
    }

    public double getKzzZGF_SZ() {
        return dKzzZGF_SZ;
    }

    public double getJjYHS_SH() {
        return dJjYHS_SH;
    }

    public double getJjGHF_SH() {
        return dJjGHF_SH;
    }

    public double getJjJSF_SH() {
        return dJjJSF_SH;
    }

    public double getJjZGF_SH() {
        return dJjZGF_SH;
    }

    public double getJjYHS_SZ() {
        return dJjYHS_SZ;
    }

    public double getJjJSF_SZ() {
        return dJjJSF_SZ;
    }

    public double getJjZGF_SZ() {
        return dJjZGF_SZ;
    }

    public double getQzJSF_SH() {
        return dQzJSF_SH;
    }

    public double getQzZGF_SH() {
        return dQzZGF_SH;
    }

    public double getQzSXF_SH() {
        return dQzSXF_SH;
    }

    public double getQzJSF_SZ() {
        return dQzJSF_SZ;
    }

    public double getQzZGF_SZ() {
        return dQzZGF_SZ;
    }

    public double getQzSXF_SZ() {
        return dQzSXF_SZ;
    }

    public double getXgJSF_SH() {
        return dXgJSF_SH;
    }

    public double getXgSXF_SH() {
        return dXgSXF_SH;
    }

    public double getXgSXF_SZ() {
        return dXgSXF_SZ;
    }

    public double getXgJSF_SZ() {
        return dXgJSF_SZ;
    }

    public double getPgJSF_SH() {
        return dPgJSF_SH;
    }

    public double getPgSXF_SH() {
        return dPgSXF_SH;
    }

    public double getPgSXF_SZ() {
        return dPgSXF_SZ;
    }

    public double getPgJSF_SZ() {
        return dPgJSF_SZ;
    }

    public double getPzJSF_SH() {
        return dPzJSF_SH;
    }

    public double getPzSXF_SH() {
        return dPzSXF_SH;
    }

    public double getPzSXF_SZ() {
        return dPzSXF_SZ;
    }

    public double getPzJSF_SZ() {
        return dPzJSF_SZ;
    }

    public double getZcZqJSF_SH() {
        return dZcZqJSF_SH;
    }

    public double getZcZqZGF_SH() {
        return dZcZqZGF_SH;
    }

    public double getZcZqSXF_SH() {
        return dZcZqSXF_SH;
    }

    public double getZcZqJSF_SZ() {
        return dZcZqJSF_SZ;
    }

    public double getZcZqZGF_SZ() {
        return dZcZqZGF_SZ;
    }

    public double getZcZqSXF_SZ() {
        return dZcZqSXF_SZ;
    }

    public double getAgYHS_SH_Min() {
        return dAgYHS_SH_Min;
    }

    public double getAgGHF_SH_Min() {
        return dAgGHF_SH_Min;
    }

    public double getAgJSF_SH_Min() {
        return dAgJSF_SH_Min;
    }

    public double getAgZGF_SH_Min() {
        return dAgZGF_SH_Min;
    }

    public double getAgYHS_SZ_Min() {
        return dAgYHS_SZ_Min;
    }

    public double getAgJSF_SZ_Min() {
        return dAgJSF_SZ_Min;
    }

    public double getAgZGF_SZ_Min() {
        return dAgZGF_SZ_Min;
    }

    public double getGzxqJSF_SH_Min() {
        return dGzxqJSF_SH_Min;
    }

    public double getGzxqZGF_SH_Min() {
        return dGzxqZGF_SH_Min;
    }

    public double getGzxqJSF_SZ_Min() {
        return dGzxqJSF_SZ_Min;
    }

    public double getGzxqZGF_SZ_Min() {
        return dGzxqZGF_SZ_Min;
    }

    public double getQyzqJSF_SH_Min() {
        return dQyzqJSF_SH_Min;
    }

    public double getQyzqZGF_SH_Min() {
        return dQyzqZGF_SH_Min;
    }

    public double getQyzqJSF_SZ_Min() {
        return dQyzqJSF_SZ_Min;
    }

    public double getQyzqZGF_SZ_Min() {
        return dQyzqZGF_SZ_Min;
    }

    public double getKzzJSF_SH_Min() {
        return dKzzJSF_SH_Min;
    }

    public double getKzzZGF_SH_Min() {
        return dKzzZGF_SH_Min;
    }

    public double getKzzJSF_SZ_Min() {
        return dKzzJSF_SZ_Min;
    }

    public double getKzzZGF_SZ_Min() {
        return dKzzZGF_SZ_Min;
    }

    public double getJjYHS_SH_Min() {
        return dJjYHS_SH_Min;
    }

    public double getJjGHF_SH_Min() {
        return dJjGHF_SH_Min;
    }

    public double getJjJSF_SH_Min() {
        return dJjJSF_SH_Min;
    }

    public double getJjZGF_SH_Min() {
        return dJjZGF_SH_Min;
    }

    public double getJjYHS_SZ_Min() {
        return dJjYHS_SZ_Min;
    }

    public double getJjJSF_SZ_Min() {
        return dJjJSF_SZ_Min;
    }

    public double getJjZGF_SZ_Min() {
        return dJjZGF_SZ_Min;
    }

    public double getGdGzxqJSF_SH() {
        return GdGzxqJSF_SH;
    }

    public double getGdGzxqJSF_SH_Min() {
        return GdGzxqJSF_SH_Min;
    }

    public double getGdGzxqZGF_SH() {
        return GdGzxqZGF_SH;
    }

    public double getGdGzxqZGF_SH_Min() {
        return GdGzxqZGF_SH_Min;
    }

    public double getGdQyzqJSF_SH() {
        return GdQyzqJSF_SH;
    }

    public double getGdQyzqJSF_SH_Min() {
        return GdQyzqJSF_SH_Min;
    }

    public double getGdQyzqZGF_SH() {
        return GdQyzqZGF_SH;
    }

    public double getGdQyzqZGF_SH_Min() {
        return GdQyzqZGF_SH_Min;
    }

    public void setAgYHS_SH(double dyhs) {
        dAgYHS_SH = dyhs;
    }

    public void setAgGHF_SH(double dghf) {
        dAgGHF_SH = dghf;
    }

    public void setAgJSF_SH(double djsf) {
        dAgJSF_SH = djsf;
    }

    public void setAgZGF_SH(double dzgf) {
        dAgZGF_SH = dzgf;
    }

    public void setAgYHS_SZ(double dyhs) {
        dAgYHS_SZ = dyhs;
    }

    public void setAgJSF_SZ(double djsf) {
        dAgJSF_SZ = djsf;
    }

    public void setAgZGF_SZ(double dzgf) {
        dAgZGF_SZ = dzgf;
    }

    public void setAgYHS_SH_Min(double dyhs) {
        dAgYHS_SH_Min = dyhs;
    }

    public void setAgGHF_SH_Min(double dghf) {
        dAgGHF_SH_Min = dghf;
    }

    public void setAgJSF_SH_Min(double djsf) {
        dAgJSF_SH_Min = djsf;
    }

    public void setAgZGF_SH_Min(double dzgf) {
        dAgZGF_SH_Min = dzgf;
    }

    public void setAgYHS_SZ_Min(double dyhs) {
        dAgYHS_SZ_Min = dyhs;
    }

    public void setAgJSF_SZ_Min(double djsf) {
        dAgJSF_SZ_Min = djsf;
    }

    public void setAgZGF_SZ_Min(double dzgf) {
        dAgZGF_SZ_Min = dzgf;
    }

    public void setGzxqJSF_SH(double djsf) {
        dGzxqJSF_SH = djsf;
    }

    public void setGzxqZGF_SH(double dzgf) {
        dGzxqZGF_SH = dzgf;
    }

    public void setGzxqJSF_SZ(double djsf) {
        dGzxqJSF_SZ = djsf;
    }

    public void setGzxqZGF_SZ(double dzgf) {
        dGzxqZGF_SZ = dzgf;
    }

    public void setGzxqJSF_SH_Min(double djsf) {
        dGzxqJSF_SH_Min = djsf;
    }

    public void setGzxqZGF_SH_Min(double dzgf) {
        dGzxqZGF_SH_Min = dzgf;
    }

    public void setGzxqJSF_SZ_Min(double djsf) {
        dGzxqJSF_SZ_Min = djsf;
    }

    public void setGzxqZGF_SZ_Min(double dzgf) {
        dGzxqZGF_SZ_Min = dzgf;
    }

    public void setQyzqJSF_SH(double djsf) {
        dQyzqJSF_SH = djsf;
    }

    public void setQyzqZGF_SH(double dzgf) {
        dQyzqZGF_SH = dzgf;
    }

    public void setQyzqJSF_SZ(double djsf) {
        dQyzqJSF_SZ = djsf;
    }

    public void setQyzqZGF_SZ(double dzgf) {
        dQyzqZGF_SZ = dzgf;
    }

    public void setQyzqJSF_SH_Min(double djsf) {
        dQyzqJSF_SH_Min = djsf;
    }

    public void setQyzqZGF_SH_Min(double dzgf) {
        dQyzqZGF_SH_Min = dzgf;
    }

    public void setQyzqJSF_SZ_Min(double djsf) {
        dQyzqJSF_SZ_Min = djsf;
    }

    public void setQyzqZGF_SZ_Min(double dzgf) {
        dQyzqZGF_SZ_Min = dzgf;
    }

    public void setKzzJSF_SH(double djsf) {
        dKzzJSF_SH = djsf;
    }

    public void setKzzZGF_SH(double dzgf) {
        dKzzZGF_SH = dzgf;
    }

    public void setKzzJSF_SZ(double djsf) {
        dKzzJSF_SZ = djsf;
    }

    public void setKzzZGF_SZ(double dzgf) {
        dKzzZGF_SZ = dzgf;
    }

    public void setKzzJSF_SH_Min(double djsf) {
        dKzzJSF_SH_Min = djsf;
    }

    public void setKzzZGF_SH_Min(double dzgf) {
        dKzzZGF_SH_Min = dzgf;
    }

    public void setKzzJSF_SZ_Min(double djsf) {
        dKzzJSF_SZ_Min = djsf;
    }

    public void setKzzZGF_SZ_Min(double dzgf) {
        dKzzZGF_SZ_Min = dzgf;
    }

    public void setJjYHS_SH(double dghf) {
        dJjYHS_SH = dghf;
    }

    public void setJjGHF_SH(double dghf) {
        dJjGHF_SH = dghf;
    }

    public void setJjJSF_SH(double djsf) {
        dJjJSF_SH = djsf;
    }

    public void setJjZGF_SH(double dzgf) {
        dJjZGF_SH = dzgf;
    }

    public void setJjYHS_SZ(double dyhs) {
        dJjYHS_SZ = dyhs;
    }

    public void setJjJSF_SZ(double djsf) {
        dJjJSF_SZ = djsf;
    }

    public void setJjZGF_SZ(double dzgf) {
        dJjZGF_SZ = dzgf;
    }

    public void setJjYHS_SH_Min(double dyhs) {
        dJjYHS_SH_Min = dyhs;
    }

    public void setJjGHF_SH_Min(double dghf) {
        dJjGHF_SH_Min = dghf;
    }

    public void setJjJSF_SH_Min(double djsf) {
        dJjJSF_SH_Min = djsf;
    }

    public void setJjZGF_SH_Min(double dzgf) {
        dJjZGF_SH_Min = dzgf;
    }

    public void setJjYHS_SZ_Min(double dyhs) {
        dJjYHS_SZ_Min = dyhs;
    }

    public void setJjJSF_SZ_Min(double djsf) {
        dJjJSF_SZ_Min = djsf;
    }

    public void setJjZGF_SZ_Min(double dzgf) {
        dJjZGF_SZ_Min = dzgf;
    }

    public void setXgJSF_SH(double djsf) {
        dXgJSF_SH = djsf;
    }

    public void setXgSXF_SH(double dsxf) {
        dXgSXF_SH = dsxf;
    }

    public void setXgSXF_SZ(double dsxf) {
        dXgSXF_SZ = dsxf;
    }

    public void setPgJSF_SH(double djsf) {
        dPgJSF_SH = djsf;
    }

    public void setPgSXF_SH(double dsxf) {
        dPgSXF_SH = dsxf;
    }

    public void setPgSXF_SZ(double dsxf) {
        dPgSXF_SZ = dsxf;
    }

    public void setPgJSF_SZ(double djsf) {
        dPgJSF_SZ = djsf;
    }

    public void setPzJSF_SH(double djsf) {
        dPzJSF_SH = djsf;
    }

    public void setPzSXF_SH(double dsxf) {
        dPzSXF_SH = dsxf;
    }

    public void setPzSXF_SZ(double dsxf) {
        dPzSXF_SZ = dsxf;
    }

    public void setPzJSF_SZ(double djsf) {
        dPzJSF_SZ = djsf;
    }

    public void setQzJSF_SH(double djsf) {
        dQzJSF_SH = djsf;
    }

    public void setQzZGF_SH(double dzgf) {
        dQzZGF_SH = dzgf;
    }

    public void setQzSXF_SH(double dsxf) {
        dQzSXF_SH = dsxf;
    }

    public void setQzJSF_SZ(double djsf) {
        dQzJSF_SZ = djsf;
    }

    public void setQzZGF_SZ(double dzgf) {
        dQzZGF_SZ = dzgf;
    }

    public void setQzSXF_SZ(double dsxf) {
        dQzSXF_SZ = dsxf;
    }

    public double getEtfGhf_SH() {
        //dETFGhf_SH = 0.00025;
        return dETFGhf_SH;
    }

    public double getEtfGhf_SZ() {
        return dETFGhf_SZ;
    }

    public void setEtfGhf_SH(double dETFGhf_SH) {
        this.dETFGhf_SH = dETFGhf_SH;
    }

    public void setEtfGhf_SZ(double dETFGhf_SZ) {
        this.dETFGhf_SZ = dETFGhf_SZ;
    }

    public void setZcZqJSF_SH(double value) {
        dZcZqJSF_SH = value;
    }

    public void setZcZqZGF_SH(double value) {
        dZcZqZGF_SH = value;
    }

    public void setZcZqSXF_SH(double value) {
        dZcZqSXF_SH = value;
    }

    public void setZcZqJSF_SZ(double value) {
        dZcZqJSF_SZ = value;
    }

    public void setZcZqZGF_SZ(double value) {
        dZcZqZGF_SZ = value;
    }

    public void setZcZqSXF_SZ(double value) {
        dZcZqSXF_SZ = value;
    }

    public void setGdGzxqJSF_SH(double value) {
        GdGzxqJSF_SH = value;
    }

    public void setGdGzxqJSF_SH_Min(double value) {
        GdGzxqJSF_SH_Min = value;
    }

    public void setGdGzxqZGF_SH(double value) {
        GdGzxqZGF_SH = value;
    }

    public void setGdGzxqZGF_SH_Min(double value) {
        GdGzxqZGF_SH_Min = value;
    }

    public void setGdQyzqJSF_SH(double value) {
        GdQyzqJSF_SH = value;
    }

    public void setGdQyzqJSF_SH_Min(double value) {
        GdQyzqJSF_SH_Min = value;
    }

    public void setGdQyzqZGF_SH(double value) {
        GdQyzqZGF_SH = value;
    }

    public void setGdQyzqZGF_SH_Min(double value) {
        GdQyzqZGF_SH_Min = value;
    }

}
