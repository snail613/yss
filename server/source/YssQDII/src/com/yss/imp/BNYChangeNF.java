package com.yss.imp;

import com.yss.util.YssD;
import com.yss.util.YssFun;
import java.util.Date;

public class BNYChangeNF {
	public BNYChangeNF() {
	}

	public static String changeTransactionTypes(String strValue) {
		if (strValue.equalsIgnoreCase("01")) {
			return "PURCHASE";
		} else if (strValue.equalsIgnoreCase("02")) {
			return "SALE";
		} else {
			return "";
		}
	}

	public static String formatString(String strSource, int intLen) {
		return formatString(strSource, intLen, " ");
	}

	public static String formatString(String strSource, int intLen, String strFill) {
		int intCount = intLen - strSource.length();
		if (intCount < 0) {
			strSource = YssFun.left(strSource, intLen);
		} else if (intCount > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < intCount; i++) {
				buf.append(strFill);
			}
			strSource = strSource + buf.toString();
		}
		return strSource;
	}

	public static String formatString(int intLen) {
		return formatString("", intLen);
	}

	public static String formatDate(Date dateSource) {
		return YssFun.formatDate(dateSource, "yyyyMMdd");
	}

	public static String formatNumber(double dubSource, int intLen, int intDigit) {
		for (int i = 0; i < intDigit; i++) {
			dubSource = YssD.mul(dubSource, 10);
		}

		String strValue = "";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < intLen; i++) {
			buf.append("0");
		}
		/*
		 * if (intDigit > 0) { buf.setLength(buf.length() - (1+intDigit));
		 * buf.append("."); for (int i = 0; i < intDigit; i++) {
		 * buf.append("#"); } }
		 */
		strValue = YssFun.formatNumber(dubSource, buf.toString());

		int intCount = intLen - strValue.length();
		if (intCount < 0) {
			strValue = YssFun.left(strValue, intLen);
		} else if (intCount > 0) {
			buf = new StringBuffer();
			for (int i = 0; i < intCount; i++) {
				buf.append("0");
			}
			strValue = buf.toString() + strValue;
		}
		return strValue;
	}

	public static String formatNumber(int intLen, int intDigit) {
		return formatNumber(0.0, intLen, intDigit);
	}

	public static String formatNumber(int intLen) {
		return formatNumber(intLen, 0);
	}

	public static String getMarket(String strMarketShort) {
		String strValue = strMarketShort;
		if (strValue.equalsIgnoreCase("HK")) {
			strValue = "HONG KONG";
		} else if (strValue.equalsIgnoreCase("SP")) {
			strValue = "SINGAPORE";
		} else if (strValue.equalsIgnoreCase("UK")) {
			strValue = "UNITED KINGDOM ";
		} else if (strValue.equalsIgnoreCase("UN")) {
			strValue = "UNITED STATES";
		} else if (strValue.equalsIgnoreCase("UW")) {
			strValue = "UNITED STATES";
		}

		else if (strValue.equalsIgnoreCase("AB")) {
			strValue = "ABU DHABI";
		} else if (strValue.equalsIgnoreCase("AD")) {
			strValue = "ANDORRA";
		} else if (strValue.equalsIgnoreCase("AE")) {
			strValue = "UNITED ARAB EMIRATES";
		} else if (strValue.equalsIgnoreCase("AF")) {
			strValue = "AFGHANISTAN";
		} else if (strValue.equalsIgnoreCase("AG")) {
			strValue = "ANTIGUA";
		} else if (strValue.equalsIgnoreCase("AI")) {
			strValue = "ANGUILLA";
		} else if (strValue.equalsIgnoreCase("AL")) {
			strValue = "ALBANIA";
		} else if (strValue.equalsIgnoreCase("AM")) {
			strValue = "ARMENIA";
		} else if (strValue.equalsIgnoreCase("AN")) {
			strValue = "NETHERLANDS ANTILLES";
		} else if (strValue.equalsIgnoreCase("AO")) {
			strValue = "ANGOLA";
		} else if (strValue.equalsIgnoreCase("AQ")) {
			strValue = "ANTARCTICA";
		} else if (strValue.equalsIgnoreCase("AR")) {
			strValue = "ARGENTINA";
		} else if (strValue.equalsIgnoreCase("AS")) {
			strValue = "AMERICAN SAMOA";
		} else if (strValue.equalsIgnoreCase("AT")) {
			strValue = "AUSTRIA";
		} else if (strValue.equalsIgnoreCase("AU")) {
			strValue = "AUSTRALIA";
		} else if (strValue.equalsIgnoreCase("AW")) {
			strValue = "ARUBA";
		} else if (strValue.equalsIgnoreCase("AZ")) {
			strValue = "AZERBAIJAN";
		} else if (strValue.equalsIgnoreCase("BA")) {
			strValue = "BOSNIA";
		} else if (strValue.equalsIgnoreCase("BB")) {
			strValue = "BARBADOS";
		} else if (strValue.equalsIgnoreCase("BD")) {
			strValue = "BANGLADESH";
		} else if (strValue.equalsIgnoreCase("BE")) {
			strValue = "BELGIUM";
		} else if (strValue.equalsIgnoreCase("BF")) {
			strValue = "BURKINA FASO";
		} else if (strValue.equalsIgnoreCase("BG")) {
			strValue = "BULGARIA";
		} else if (strValue.equalsIgnoreCase("BH")) {
			strValue = "BAHRAIN";
		} else if (strValue.equalsIgnoreCase("BI")) {
			strValue = "BURUNDI";
		} else if (strValue.equalsIgnoreCase("BJ")) {
			strValue = "BENIN";
		} else if (strValue.equalsIgnoreCase("BM")) {
			strValue = "BERMUDA";
		} else if (strValue.equalsIgnoreCase("BN")) {
			strValue = "BRUNEI";
		} else if (strValue.equalsIgnoreCase("BO")) {
			strValue = "BOLIVIA";
		} else if (strValue.equalsIgnoreCase("BR")) {
			strValue = "BRAZIL";
		} else if (strValue.equalsIgnoreCase("BS")) {
			strValue = "BAHAMAS ";
		} else if (strValue.equalsIgnoreCase("BT")) {
			strValue = "BHUTAN";
		} else if (strValue.equalsIgnoreCase("BU")) {
			strValue = "BURMA";
		} else if (strValue.equalsIgnoreCase("BV")) {
			strValue = "BOUVET ISLAND";
		} else if (strValue.equalsIgnoreCase("BW")) {
			strValue = "BOTSWANA";
		} else if (strValue.equalsIgnoreCase("BY")) {
			strValue = "BYELORUSSIA";
		} else if (strValue.equalsIgnoreCase("BZ")) {
			strValue = "BELIZE";
		} else if (strValue.equalsIgnoreCase("CA")) {
			strValue = "CANADA";
		} else if (strValue.equalsIgnoreCase("CC")) {
			strValue = "COCOS ISLANDS";
		} else if (strValue.equalsIgnoreCase("CF")) {
			strValue = "CENTRAL AFRICAN REP";
		} else if (strValue.equalsIgnoreCase("CG")) {
			strValue = "CONGO";
		} else if (strValue.equalsIgnoreCase("CH")) {
			strValue = "SWITZERLAND";
		} else if (strValue.equalsIgnoreCase("CK")) {
			strValue = "COOK ISLANDS";
		} else if (strValue.equalsIgnoreCase("CL")) {
			strValue = "CHILE";
		} else if (strValue.equalsIgnoreCase("CM")) {
			strValue = "UNITED REP CAMEROON";
		} else if (strValue.equalsIgnoreCase("CN")) {
			strValue = "CHINA";
		} else if (strValue.equalsIgnoreCase("CO")) {
			strValue = "COLOMBIA";
		} else if (strValue.equalsIgnoreCase("CR")) {
			strValue = "COSTA RICA";
		} else if (strValue.equalsIgnoreCase("CT")) {
			strValue = "CANTON AND ENDERBURY";
		} else if (strValue.equalsIgnoreCase("CU")) {
			strValue = "CUBA";
		} else if (strValue.equalsIgnoreCase("CV")) {
			strValue = "CAPE VERDE";
		} else if (strValue.equalsIgnoreCase("CX")) {
			strValue = "CHRISTMAS ISLANDS";
		} else if (strValue.equalsIgnoreCase("CY")) {
			strValue = "CYPRUS";
		} else if (strValue.equalsIgnoreCase("CZ")) {
			strValue = "CZECH REPUBLIC";
		} else if (strValue.equalsIgnoreCase("DE")) {
			strValue = "GERMANY";
		} else if (strValue.equalsIgnoreCase("DJ")) {
			strValue = "DJIBOUTI";
		} else if (strValue.equalsIgnoreCase("DK")) {
			strValue = "DENMARK";
		} else if (strValue.equalsIgnoreCase("DM")) {
			strValue = "DOMINICA";
		} else if (strValue.equalsIgnoreCase("DO")) {
			strValue = "DOMINICAN REPUBLIC";
		} else if (strValue.equalsIgnoreCase("DZ")) {
			strValue = "ALGERIA";
		} else if (strValue.equalsIgnoreCase("EC")) {
			strValue = "ECUADOR";
		} else if (strValue.equalsIgnoreCase("EE")) {
			strValue = "ESTONIA";
		} else if (strValue.equalsIgnoreCase("EG")) {
			strValue = "EGYPT";
		} else if (strValue.equalsIgnoreCase("EH")) {
			strValue = "WESTERN SAHARA";
		} else if (strValue.equalsIgnoreCase("ES")) {
			strValue = "SPAIN";
		} else if (strValue.equalsIgnoreCase("ET")) {
			strValue = "ETHIOPIA";
		} else if (strValue.equalsIgnoreCase("EU")) {
			strValue = "EUROMARKET";
		} else if (strValue.equalsIgnoreCase("FI")) {
			strValue = "FINLAND";
		} else if (strValue.equalsIgnoreCase("FJ")) {
			strValue = "FIJI";
		} else if (strValue.equalsIgnoreCase("FK")) {
			strValue = "FALKLAND ISLANDS";
		} else if (strValue.equalsIgnoreCase("FM")) {
			strValue = "MICRONESIA";
		} else if (strValue.equalsIgnoreCase("FO")) {
			strValue = "FAEROE ISLANDS";
		} else if (strValue.equalsIgnoreCase("FR")) {
			strValue = "FRANCE";
		} else if (strValue.equalsIgnoreCase("GA")) {
			strValue = "GABON";
		} else if (strValue.equalsIgnoreCase("GB")) {
			strValue = "UNITED KINGDOM";
		} else if (strValue.equalsIgnoreCase("GD")) {
			strValue = "GRENADA";
		} else if (strValue.equalsIgnoreCase("GE")) {
			strValue = "GEORGIA";
		} else if (strValue.equalsIgnoreCase("GF")) {
			strValue = "FRENCH GUIANA";
		} else if (strValue.equalsIgnoreCase("GG")) {
			strValue = "GUERNSEY";
		} else if (strValue.equalsIgnoreCase("GH")) {
			strValue = "GHANA ";
		} else if (strValue.equalsIgnoreCase("GI")) {
			strValue = "GIBRALTAR";
		} else if (strValue.equalsIgnoreCase("GL")) {
			strValue = "GREENLAND";
		} else if (strValue.equalsIgnoreCase("GM")) {
			strValue = "GAMBIA";
		} else if (strValue.equalsIgnoreCase("GN")) {
			strValue = "GUINEA";
		} else if (strValue.equalsIgnoreCase("GP")) {
			strValue = "GUADELOUPE";
		} else if (strValue.equalsIgnoreCase("GQ")) {
			strValue = "EQUATORIAL GUINEA";
		} else if (strValue.equalsIgnoreCase("GR")) {
			strValue = "GREECE";
		} else if (strValue.equalsIgnoreCase("GT")) {
			strValue = "GUATEMALA";
		} else if (strValue.equalsIgnoreCase("GU")) {
			strValue = "GUAM";
		} else if (strValue.equalsIgnoreCase("GW")) {
			strValue = "GUINEA-BISSAU";
		} else if (strValue.equalsIgnoreCase("GY")) {
			strValue = "GUYANA";
		} else if (strValue.equalsIgnoreCase("HK")) {
			strValue = "HONG KONG";
		} else if (strValue.equalsIgnoreCase("HM")) {
			strValue = "HEARD & MCDONALD ISL";
		} else if (strValue.equalsIgnoreCase("HN")) {
			strValue = "HONDURAS";
		} else if (strValue.equalsIgnoreCase("HR")) {
			strValue = "CROATIA";
		} else if (strValue.equalsIgnoreCase("HT")) {
			strValue = "HAITI";
		} else if (strValue.equalsIgnoreCase("HU")) {
			strValue = "HUNGARY";
		} else if (strValue.equalsIgnoreCase("HV")) {
			strValue = "UPPER VOLTA";
		} else if (strValue.equalsIgnoreCase("ID")) {
			strValue = "INDONESIA";
		} else if (strValue.equalsIgnoreCase("IE")) {
			strValue = "IRELAND";
		} else if (strValue.equalsIgnoreCase("IL")) {
			strValue = "ISRAEL";
		} else if (strValue.equalsIgnoreCase("IM")) {
			strValue = "ISLE OF MAN";
		} else if (strValue.equalsIgnoreCase("IN")) {
			strValue = "INDIA";
		} else if (strValue.equalsIgnoreCase("IO")) {
			strValue = "BRITISH IND OCE TERR";
		} else if (strValue.equalsIgnoreCase("IQ")) {
			strValue = "IRAQ";
		} else if (strValue.equalsIgnoreCase("IR")) {
			strValue = "IRAN";
		} else if (strValue.equalsIgnoreCase("IS")) {
			strValue = "ICELAND";
		} else if (strValue.equalsIgnoreCase("IT")) {
			strValue = "ITALY";
		} else if (strValue.equalsIgnoreCase("JE")) {
			strValue = "JERSEY";
		} else if (strValue.equalsIgnoreCase("JM")) {
			strValue = "JAMAICA";
		} else if (strValue.equalsIgnoreCase("JO")) {
			strValue = "JORDAN";
		} else if (strValue.equalsIgnoreCase("JP")) {
			strValue = "JAPAN";
		} else if (strValue.equalsIgnoreCase("JT")) {
			strValue = "JOHNSTON ISLAND";
		} else if (strValue.equalsIgnoreCase("KE")) {
			strValue = "KENYA";
		} else if (strValue.equalsIgnoreCase("KH")) {
			strValue = "KAMPUCHEA";
		} else if (strValue.equalsIgnoreCase("KI")) {
			strValue = "KIRIBATI";
		} else if (strValue.equalsIgnoreCase("KM")) {
			strValue = "COMOROS";
		} else if (strValue.equalsIgnoreCase("KN")) {
			strValue = "ST.KITTS-NEVIS-ANG  ";
		} else if (strValue.equalsIgnoreCase("KP")) {
			strValue = "DEM PEOPLE RP KOREA";
		} else if (strValue.equalsIgnoreCase("KR")) {
			strValue = "SOUTH KOREA";
		} else if (strValue.equalsIgnoreCase("KW")) {
			strValue = "KUWAIT";
		} else if (strValue.equalsIgnoreCase("KY")) {
			strValue = "CAYMAN ISLANDS";
		} else if (strValue.equalsIgnoreCase("KZ")) {
			strValue = "KAZAKHSTAN";
		} else if (strValue.equalsIgnoreCase("LA")) {
			strValue = "LAOS";
		} else if (strValue.equalsIgnoreCase("LB")) {
			strValue = "LEBANON";
		} else if (strValue.equalsIgnoreCase("LC")) {
			strValue = "ST.LUCIA  ";
		} else if (strValue.equalsIgnoreCase("LI")) {
			strValue = "LIECHTENSTEIN";
		} else if (strValue.equalsIgnoreCase("LK")) {
			strValue = "SRI LANKA";
		} else if (strValue.equalsIgnoreCase("LR")) {
			strValue = "LIBERIA";
		} else if (strValue.equalsIgnoreCase("LS")) {
			strValue = "LESOTHO";
		} else if (strValue.equalsIgnoreCase("LT")) {
			strValue = "LITHUANIA";
		} else if (strValue.equalsIgnoreCase("LU")) {
			strValue = "LUXEMBOURG";
		} else if (strValue.equalsIgnoreCase("LV")) {
			strValue = "LATVIA";
		} else if (strValue.equalsIgnoreCase("LY")) {
			strValue = "LIBYA";
		} else if (strValue.equalsIgnoreCase("MA")) {
			strValue = "MOROCCO";
		} else if (strValue.equalsIgnoreCase("MC")) {
			strValue = "MONACO";
		} else if (strValue.equalsIgnoreCase("MG")) {
			strValue = "MADAGASCAR";
		} else if (strValue.equalsIgnoreCase("MH")) {
			strValue = "MARSHALL ISLANDS";
		} else if (strValue.equalsIgnoreCase("MI")) {
			strValue = "MIDWAY ISLANDS";
		} else if (strValue.equalsIgnoreCase("ML")) {
			strValue = "MALI";
		} else if (strValue.equalsIgnoreCase("MN")) {
			strValue = "MONGOLIA";
		} else if (strValue.equalsIgnoreCase("MO")) {
			strValue = "MACAU";
		} else if (strValue.equalsIgnoreCase("MP")) {
			strValue = "NORTHERN MARIANA ISL";
		} else if (strValue.equalsIgnoreCase("MQ")) {
			strValue = "MARTINIQUE";
		} else if (strValue.equalsIgnoreCase("MR")) {
			strValue = "MAURITANIA";
		} else if (strValue.equalsIgnoreCase("MS")) {
			strValue = "MONTSERRAT";
		} else if (strValue.equalsIgnoreCase("MT")) {
			strValue = "MALTA";
		} else if (strValue.equalsIgnoreCase("MU")) {
			strValue = "MAURITIUS";
		} else if (strValue.equalsIgnoreCase("MV")) {
			strValue = "MALDIVES";
		} else if (strValue.equalsIgnoreCase("MW")) {
			strValue = "MALAWI";
		} else if (strValue.equalsIgnoreCase("MX")) {
			strValue = "MEXICO";
		} else if (strValue.equalsIgnoreCase("MY")) {
			strValue = "MALAYSIA";
		} else if (strValue.equalsIgnoreCase("MZ")) {
			strValue = "MOZAMBIQUE";
		} else if (strValue.equalsIgnoreCase("NA")) {
			strValue = "NAMIBIA";
		} else if (strValue.equalsIgnoreCase("NC")) {
			strValue = "NEW CALEDONIA";
		} else if (strValue.equalsIgnoreCase("NE")) {
			strValue = "NIGER";
		} else if (strValue.equalsIgnoreCase("NF")) {
			strValue = "NORFOLK ISLAND";
		} else if (strValue.equalsIgnoreCase("NG")) {
			strValue = "NIGERIA";
		} else if (strValue.equalsIgnoreCase("NI")) {
			strValue = "NICARAGUA";
		} else if (strValue.equalsIgnoreCase("NL")) {
			strValue = "NETHERLANDS";
		} else if (strValue.equalsIgnoreCase("NM")) {
			strValue = "<NO MARKET>";
		} else if (strValue.equalsIgnoreCase("NO")) {
			strValue = "NORWAY";
		} else if (strValue.equalsIgnoreCase("NP")) {
			strValue = "NEPAL";
		} else if (strValue.equalsIgnoreCase("NQ")) {
			strValue = "DRONNING MAUD LAND";
		} else if (strValue.equalsIgnoreCase("NR")) {
			strValue = "NAURU";
		} else if (strValue.equalsIgnoreCase("NT")) {
			strValue = "NEUTRAL ZONE";
		} else if (strValue.equalsIgnoreCase("NU")) {
			strValue = "NIUE";
		} else if (strValue.equalsIgnoreCase("NZ")) {
			strValue = "NEW ZEALAND";
		} else if (strValue.equalsIgnoreCase("OM")) {
			strValue = "OMAN";
		} else if (strValue.equalsIgnoreCase("PA")) {
			strValue = "PANAMA";
		} else if (strValue.equalsIgnoreCase("PC")) {
			strValue = "PACIFIC ISLANDS";
		} else if (strValue.equalsIgnoreCase("PE")) {
			strValue = "PERU";
		} else if (strValue.equalsIgnoreCase("PF")) {
			strValue = "FRENCH POLYNESIA";
		} else if (strValue.equalsIgnoreCase("PG")) {
			strValue = "PAPUA NEW GUINEA";
		} else if (strValue.equalsIgnoreCase("PH")) {
			strValue = "PHILIPPINES";
		} else if (strValue.equalsIgnoreCase("PK")) {
			strValue = "PAKISTAN";
		} else if (strValue.equalsIgnoreCase("PL")) {
			strValue = "POLAND";
		} else if (strValue.equalsIgnoreCase("PM")) {
			strValue = "ST.PIERRE MIQUELON";
		} else if (strValue.equalsIgnoreCase("PN")) {
			strValue = "PITCAIRN ISLAND";
		} else if (strValue.equalsIgnoreCase("PR")) {
			strValue = "PUERTO RICO";
		} else if (strValue.equalsIgnoreCase("PT")) {
			strValue = "PORTUGAL";
		} else if (strValue.equalsIgnoreCase("PU")) {
			strValue = "US MISC. PACIFIC ISL";
		} else if (strValue.equalsIgnoreCase("PY")) {
			strValue = "PARAGUAY";
		} else if (strValue.equalsIgnoreCase("PZ")) {
			strValue = "PANAMA CANAL ZONE";
		} else if (strValue.equalsIgnoreCase("QA")) {
			strValue = "QATAR";
		} else if (strValue.equalsIgnoreCase("RE")) {
			strValue = "REUNION";
		} else if (strValue.equalsIgnoreCase("RO")) {
			strValue = "ROMANIA";
		} else if (strValue.equalsIgnoreCase("RS")) {
			strValue = "REPUBLIC OF SERBIA";
		} else if (strValue.equalsIgnoreCase("RU")) {
			strValue = "RUSSIA";
		} else if (strValue.equalsIgnoreCase("RW")) {
			strValue = "RWANDA";
		} else if (strValue.equalsIgnoreCase("SA")) {
			strValue = "SAUDI ARABIA";
		} else if (strValue.equalsIgnoreCase("SB")) {
			strValue = "SOLOMON ISLANDS";
		} else if (strValue.equalsIgnoreCase("SC")) {
			strValue = "SEYCHELLES";
		} else if (strValue.equalsIgnoreCase("SD")) {
			strValue = "SUDAN";
		} else if (strValue.equalsIgnoreCase("SE")) {
			strValue = "SWEDEN";
		} else if (strValue.equalsIgnoreCase("SG")) {
			strValue = "SINGAPORE";
		} else if (strValue.equalsIgnoreCase("SH")) {
			strValue = "ST.HELENA";
		} else if (strValue.equalsIgnoreCase("SI")) {
			strValue = "SLOVENIA";
		} else if (strValue.equalsIgnoreCase("SJ")) {
			strValue = "SVALBARD J MAYEN ISL";
		} else if (strValue.equalsIgnoreCase("SK")) {
			strValue = "SLOVAK REPUBLIC";
		} else if (strValue.equalsIgnoreCase("SL")) {
			strValue = "SIERRA LEONE";
		} else if (strValue.equalsIgnoreCase("SM")) {
			strValue = "SAN MARINO";
		} else if (strValue.equalsIgnoreCase("SN")) {
			strValue = "SENEGAL";
		} else if (strValue.equalsIgnoreCase("SO")) {
			strValue = "SOMALIA";
		} else if (strValue.equalsIgnoreCase("SR")) {
			strValue = "SURINAM";
		} else if (strValue.equalsIgnoreCase("ST")) {
			strValue = "SAO TOME AND PRINCIP";
		} else if (strValue.equalsIgnoreCase("SV")) {
			strValue = "EL SALVADOR";
		} else if (strValue.equalsIgnoreCase("SY")) {
			strValue = "SYRIAN ARAB REPUBLIC";
		} else if (strValue.equalsIgnoreCase("SZ")) {
			strValue = "SWAZILAND";
		} else if (strValue.equalsIgnoreCase("TC")) {
			strValue = "TURKS & CAICOS ISL";
		} else if (strValue.equalsIgnoreCase("TD")) {
			strValue = "CHAD";
		} else if (strValue.equalsIgnoreCase("TF")) {
			strValue = "FRENCH SOUTHERN TERR";
		} else if (strValue.equalsIgnoreCase("TG")) {
			strValue = "TOGO";
		} else if (strValue.equalsIgnoreCase("TH")) {
			strValue = "THAILAND";
		} else if (strValue.equalsIgnoreCase("TK")) {
			strValue = "TOKELAU";
		} else if (strValue.equalsIgnoreCase("TM")) {
			strValue = "TURKMENIA";
		} else if (strValue.equalsIgnoreCase("TN")) {
			strValue = "TUNISIA";
		} else if (strValue.equalsIgnoreCase("TO")) {
			strValue = "TONGA";
		} else if (strValue.equalsIgnoreCase("TP")) {
			strValue = "EAST TIMOR";
		} else if (strValue.equalsIgnoreCase("TR")) {
			strValue = "TURKEY";
		} else if (strValue.equalsIgnoreCase("TT")) {
			strValue = "TRINIDAD AND TOBAGO";
		} else if (strValue.equalsIgnoreCase("TV")) {
			strValue = "TUVALU";
		} else if (strValue.equalsIgnoreCase("TW")) {
			strValue = "TAIWAN";
		} else if (strValue.equalsIgnoreCase("TZ")) {
			strValue = "TANZANIA";
		} else if (strValue.equalsIgnoreCase("UA")) {
			strValue = "UKRAINIAN";
		} else if (strValue.equalsIgnoreCase("UG")) {
			strValue = "UGANDA";
		} else if (strValue.equalsIgnoreCase("UM")) {
			strValue = "U.S. MINOR OUT ISL";
		} else if (strValue.equalsIgnoreCase("US")) {
			strValue = "UNITED STATES";
		} else if (strValue.equalsIgnoreCase("UY")) {
			strValue = "URUGUAY";
		} else if (strValue.equalsIgnoreCase("UZ")) {
			strValue = "UZBEKISTAN";
		} else if (strValue.equalsIgnoreCase("VA")) {
			strValue = "VATICAN CITY STATE";
		} else if (strValue.equalsIgnoreCase("VC")) {
			strValue = "ST.VINCE GRENADINES";
		} else if (strValue.equalsIgnoreCase("VE")) {
			strValue = "VENEZUELA";
		} else if (strValue.equalsIgnoreCase("VG")) {
			strValue = "BRITISH VIRGIN ISLND";
		} else if (strValue.equalsIgnoreCase("VI")) {
			strValue = "US VIRGIN ISLANDS";
		} else if (strValue.equalsIgnoreCase("VN")) {
			strValue = "VIETNAM";
		} else if (strValue.equalsIgnoreCase("VU")) {
			strValue = "VANUATU";
		} else if (strValue.equalsIgnoreCase("WF")) {
			strValue = "WALLIS & FUTUNA ISL";
		} else if (strValue.equalsIgnoreCase("WK")) {
			strValue = "WAKE ISLAND";
		} else if (strValue.equalsIgnoreCase("WS")) {
			strValue = "SAMOA";
		} else if (strValue.equalsIgnoreCase("XO")) {
			strValue = "IVORY COAST";
		} else if (strValue.equalsIgnoreCase("XX")) {
			strValue = "NOMATCH";
		} else if (strValue.equalsIgnoreCase("YD")) {
			strValue = "YEMEN DEMOCRATIC";
		} else if (strValue.equalsIgnoreCase("YE")) {
			strValue = "YEMEN";
		} else if (strValue.equalsIgnoreCase("YU")) {
			strValue = "YUGOSLAVIA";
		} else if (strValue.equalsIgnoreCase("ZA")) {
			strValue = "SOUTH AFRICA";
		} else if (strValue.equalsIgnoreCase("ZM")) {
			strValue = "ZAMBIA";
		} else if (strValue.equalsIgnoreCase("ZR")) {
			strValue = "ZAIRE";
		} else if (strValue.equalsIgnoreCase("ZW")) {
			strValue = "ZIMBABWE";
		} else if (strValue.equalsIgnoreCase("AA")) {
			strValue = "ALL MARKETS";
		}

		return strValue;
	}

	public static String getCurrency(String strCurrencyShort) {
		String strValue = strCurrencyShort;
		if (strValue.equalsIgnoreCase("AED")) {
			strValue = "UAE DIRHAM";
		} else if (strValue.equalsIgnoreCase("ARS")) {
			strValue = "ARGENTINE PESO";
		} else if (strValue.equalsIgnoreCase("ATS")) {
			strValue = "AUSTRIAN SCHILLING";
		} else if (strValue.equalsIgnoreCase("AUD")) {
			strValue = "AUSTRALIAN DOLLAR";
		} else if (strValue.equalsIgnoreCase("BBD")) {
			strValue = "BARBADOS DOLLAR";
		} else if (strValue.equalsIgnoreCase("BDT")) {
			strValue = "BANGLADESH TAKA";
		} else if (strValue.equalsIgnoreCase("BEF")) {
			strValue = "BELGIAN FRANC";
		} else if (strValue.equalsIgnoreCase("BGL")) {
			strValue = "BULGARIAN LEV";
		} else if (strValue.equalsIgnoreCase("BHD")) {
			strValue = "BAHRAINI DINAR";
		} else if (strValue.equalsIgnoreCase("BMD")) {
			strValue = "BERMUDIAN DOLLAR";
		} else if (strValue.equalsIgnoreCase("BOB")) {
			strValue = "BOLIVIANO";
		} else if (strValue.equalsIgnoreCase("BRL")) {
			strValue = "BRAZILIAN REAL";
		} else if (strValue.equalsIgnoreCase("BRR")) {
			strValue = "CRUZERIO REAL";
		} else if (strValue.equalsIgnoreCase("BWP")) {
			strValue = "BOTSWANIAN PULA";
		} else if (strValue.equalsIgnoreCase("CAD")) {
			strValue = "CANADIAN DOLLAR";
		} else if (strValue.equalsIgnoreCase("CHF")) {
			strValue = "SWISS FRANC";
		} else if (strValue.equalsIgnoreCase("CLP")) {
			strValue = "CHILEAN PESO";
		} else if (strValue.equalsIgnoreCase("CNY")) {
			strValue = "CHINESE YUAN";
		} else if (strValue.equalsIgnoreCase("COP")) {
			strValue = "COLOMBIAN PESO";
		} else if (strValue.equalsIgnoreCase("CRC")) {
			strValue = "COSTA RICAN COLON";
		} else if (strValue.equalsIgnoreCase("CYP")) {
			strValue = "CYPRUS POUND";
		} else if (strValue.equalsIgnoreCase("CZK")) {
			strValue = "CZECH KORUNA";
		} else if (strValue.equalsIgnoreCase("DEM")) {
			strValue = "DEUTSCHE MARK";
		} else if (strValue.equalsIgnoreCase("DKK")) {
			strValue = "DANISH KRONE";
		} else if (strValue.equalsIgnoreCase("ECS")) {
			strValue = "ECUADORAN SUCRE";
		} else if (strValue.equalsIgnoreCase("EEK")) {
			strValue = "ESTONIAN KROON";
		} else if (strValue.equalsIgnoreCase("EGP")) {
			strValue = "EGYPTIAN POUND";
		} else if (strValue.equalsIgnoreCase("ESP")) {
			strValue = "SPANISH PESETA";
		} else if (strValue.equalsIgnoreCase("EUR")) {
			strValue = "EURO";
		} else if (strValue.equalsIgnoreCase("FIM")) {
			strValue = "FINNISH MARKKA";
		} else if (strValue.equalsIgnoreCase("FRF")) {
			strValue = "FRENCH FRANC";
		} else if (strValue.equalsIgnoreCase("GBP")) {
			strValue = "U.K. POUND STERLING";
		} else if (strValue.equalsIgnoreCase("GHC")) {
			strValue = "GHANANIAN CEDI";
		} else if (strValue.equalsIgnoreCase("GRD")) {
			strValue = "GREEK DRACHMA";
		} else if (strValue.equalsIgnoreCase("GYD")) {
			strValue = "GUYANA DOLLAR";
		} else if (strValue.equalsIgnoreCase("HKD")) {
			strValue = "HONG KONG DOLLAR";
		} else if (strValue.equalsIgnoreCase("HNL")) {
			strValue = "HONDURAN LEMPIRA";
		} else if (strValue.equalsIgnoreCase("HRK")) {
			strValue = "CROATIAN KUNA";
		} else if (strValue.equalsIgnoreCase("HUF")) {
			strValue = "HUNGARIAN FORINT";
		} else if (strValue.equalsIgnoreCase("IDR")) {
			strValue = "INDONESIAN RUPIAH";
		} else if (strValue.equalsIgnoreCase("IEP")) {
			strValue = "IRISH POUND";
		} else if (strValue.equalsIgnoreCase("ILS")) {
			strValue = "ISRAELI SHEKEL";
		} else if (strValue.equalsIgnoreCase("INR")) {
			strValue = "INDIAN RUPEE";
		} else if (strValue.equalsIgnoreCase("IRR")) {
			strValue = "IRANIAN RIAL";
		} else if (strValue.equalsIgnoreCase("ISK")) {
			strValue = "ICELANDIC KRONA";
		} else if (strValue.equalsIgnoreCase("ITL")) {
			strValue = "ITALIAN LIRA";
		} else if (strValue.equalsIgnoreCase("JMD")) {
			strValue = "JAMAICAN DOLLAR";
		} else if (strValue.equalsIgnoreCase("JOD")) {
			strValue = "JORDANIAN DINAR";
		} else if (strValue.equalsIgnoreCase("JPY")) {
			strValue = "JAPANESE YEN";
		} else if (strValue.equalsIgnoreCase("KES")) {
			strValue = "KENYAN SHILLING";
		} else if (strValue.equalsIgnoreCase("KRW")) {
			strValue = "SOUTH KOREAN WON";
		} else if (strValue.equalsIgnoreCase("KWD")) {
			strValue = "KUWAITI DINAR";
		} else if (strValue.equalsIgnoreCase("KZT")) {
			strValue = "KAZAKHSTANI TENGE";
		} else if (strValue.equalsIgnoreCase("LKR")) {
			strValue = "SRI LANKA RUPEE";
		} else if (strValue.equalsIgnoreCase("LTL")) {
			strValue = "LITHUANIAN LITAS";
		} else if (strValue.equalsIgnoreCase("LUF")) {
			strValue = "LUXEMBOURG FRANC";
		} else if (strValue.equalsIgnoreCase("LVL")) {
			strValue = "LATVIAN LATS";
		} else if (strValue.equalsIgnoreCase("MAD")) {
			strValue = "MOROCCAN DIRHAM";
		} else if (strValue.equalsIgnoreCase("MGF")) {
			strValue = "MALAGASY FRANC";
		} else if (strValue.equalsIgnoreCase("MTL")) {
			strValue = "MALTESE LIRA";
		} else if (strValue.equalsIgnoreCase("MUR")) {
			strValue = "MAURITIUS RUPEE";
		} else if (strValue.equalsIgnoreCase("MXN")) {
			strValue = "MEXICAN NUEVO PESO";
		} else if (strValue.equalsIgnoreCase("MYR")) {
			strValue = "MALAYSIAN RINGGIT";
		} else if (strValue.equalsIgnoreCase("NGN")) {
			strValue = "NIGERIAN NAIRA";
		} else if (strValue.equalsIgnoreCase("NLG")) {
			strValue = "NETHERLANDS GUILDER";
		} else if (strValue.equalsIgnoreCase("NOK")) {
			strValue = "NORWEGIAN KRONE";
		} else if (strValue.equalsIgnoreCase("NZD")) {
			strValue = "NEW ZEALAND DOLLAR";
		} else if (strValue.equalsIgnoreCase("OMR")) {
			strValue = "OMANI RIAL";
		} else if (strValue.equalsIgnoreCase("PAB")) {
			strValue = "BALBOA";
		} else if (strValue.equalsIgnoreCase("PEN")) {
			strValue = "PERUVIAN NEW SOL";
		} else if (strValue.equalsIgnoreCase("PHP")) {
			strValue = "PHILIPPINES PESO";
		} else if (strValue.equalsIgnoreCase("PKR")) {
			strValue = "PAKISTANI RUPEE";
		} else if (strValue.equalsIgnoreCase("PLN")) {
			strValue = "POLISH ZLOTY";
		} else if (strValue.equalsIgnoreCase("PTE")) {
			strValue = "PORTUGUESE ESCUDO";
		} else if (strValue.equalsIgnoreCase("QAR")) {
			strValue = "QATAR RIYAL";
		} else if (strValue.equalsIgnoreCase("RON")) {
			strValue = "NEW ROMANIAN LEU";
		} else if (strValue.equalsIgnoreCase("RSD")) {
			strValue = "SERBIAN DINAR";
		} else if (strValue.equalsIgnoreCase("RUB")) {
			strValue = "RUSSIAN RUBLE";
		} else if (strValue.equalsIgnoreCase("SAR")) {
			strValue = "SAUDI RIYAL";
		} else if (strValue.equalsIgnoreCase("SEK")) {
			strValue = "SWEDISH KRONA";
		} else if (strValue.equalsIgnoreCase("SGD")) {
			strValue = "SINGAPORE DOLLAR";
		} else if (strValue.equalsIgnoreCase("SKK")) {
			strValue = "SLOVAKIAN KORUNA";
		} else if (strValue.equalsIgnoreCase("SZL")) {
			strValue = "SWAZILAND LILANGENI";
		} else if (strValue.equalsIgnoreCase("THB")) {
			strValue = "THAI BAHT";
		} else if (strValue.equalsIgnoreCase("TND")) {
			strValue = "TUNISIAN DINAR";
		} else if (strValue.equalsIgnoreCase("TRY")) {
			strValue = "NEW TURKISH LIRA";
		} else if (strValue.equalsIgnoreCase("TWD")) {
			strValue = "TAIWAN NEW DOLLAR";
		} else if (strValue.equalsIgnoreCase("UAH")) {
			strValue = "UKRAINIAN HRYVANIA";
		} else if (strValue.equalsIgnoreCase("UAK")) {
			strValue = "UKRAINE KARBOUANET";
		} else if (strValue.equalsIgnoreCase("UGX")) {
			strValue = "UGANDA SCHILLING";
		} else if (strValue.equalsIgnoreCase("USD")) {
			strValue = "UNITED STATES DOLLAR";
		} else if (strValue.equalsIgnoreCase("UYU")) {
			strValue = "URUGUAYAN PESO";
		} else if (strValue.equalsIgnoreCase("VEB")) {
			strValue = "VENEZUELAN BOLIVAR";
		} else if (strValue.equalsIgnoreCase("XEU")) {
			strValue = "EUROPEAN CCY UNIT";
		} else if (strValue.equalsIgnoreCase("XOF")) {
			strValue = "IVORY COAST/MALI";
		} else if (strValue.equalsIgnoreCase("XXX")) {
			strValue = "NOMATCH";
		} else if (strValue.equalsIgnoreCase("ZAL")) {
			strValue = "FIN SO AFRICAN RAND";
		} else if (strValue.equalsIgnoreCase("ZAR")) {
			strValue = "SOUTH AFRICAN RAND";
		} else if (strValue.equalsIgnoreCase("ZMK")) {
			strValue = "ZAMBIAN KWACHA";
		} else if (strValue.equalsIgnoreCase("ZRN")) {
			strValue = "NEW ZAIRE";
		} else if (strValue.equalsIgnoreCase("ZWD")) {
			strValue = "ZIMBABWEAN DOLLAR";
		}
		return strValue;
	}

	public static String getPlaceOfSettlement(String strMarketShort) {
		String strValue = strMarketShort;
		if (strValue.equalsIgnoreCase("HK")) {
			strValue = "XHKCHKH1";
		} else if (strValue.equalsIgnoreCase("US")) {
			strValue = "DTCYUS33";
		} else if (strValue.equalsIgnoreCase("TH")) {
			strValue = "TSDCTHBK";
		} else if (strValue.equalsIgnoreCase("GR")) {
			strValue = "DAKVDEFFXXX";
		}else {
			strValue = "CEDELULL";
		}
		return strValue;
	}

	public static String getPlaceOfSafekeeping(String strMarketShort) {
		String strValue = strMarketShort;
		if (strValue.equalsIgnoreCase("HK")) {
			strValue = "HSBCHKHH";
		} else if (strValue.equalsIgnoreCase("US")) {
			strValue = "IRVTUS3N";
		} else if (strValue.equalsIgnoreCase("TH")) {
			strValue = "HSBCTHBK";
			
		} else if (strValue.equalsIgnoreCase("GR")) {
			strValue = "DAKVDEFFXXX";
		} 
		             
		else {
			strValue = "CEDELULL";
		}
		return strValue;
	}

	public static String getBrokerAccount(String strBroker, String strMarketShort) {
		String strValue;
		if (strBroker.equalsIgnoreCase("CICC")) {
			strValue = "";
		} else if (strBroker.equalsIgnoreCase("CS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "A300001";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "79G000015";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "GSHK10055751";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "011455391";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("JP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "01 CLEARING A/C";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "092668";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("UBS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("MS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CGMI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "A/C 810001";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("RBC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "4000162";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GTJA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CANTOR")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("FSSL")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "6470";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCOM")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("HTHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BNP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("KGI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "A/C NO G 68254";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("ML")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "For sub a/c: MLPFNS(BIC code:MLPFUS31)";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "SUB ACCOUNT 85S08R73";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CLSA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "A/C CLSA Ltd";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else {
			strValue = "";
		}
		return strValue;
	}

	public static String getBrokerID(String strBroker, String strMarketShort) {
		String strValue;
		if (strBroker.equalsIgnoreCase("CICC")) {
			strValue = "CICRHKH1";
		} else if (strBroker.equalsIgnoreCase("CS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CSFBHKHX";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "CSFBHKHX";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "GSILGB2X";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "GSILGB2X";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("JP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CHASHKAL";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "CHASHKAL";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("UBS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "SWDRHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "UBSWUS33";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "BCGSHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("MS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "MSSEHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0050";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CAREHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CGMI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "SBHKHKHH";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "SBSIUS33";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("RBC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "ROYCCAT2";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GTJA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "GJSHHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CANTOR")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CFCMHKHH";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "7252";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("FSSL")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01686";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01601";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCOM")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01842";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("HTHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01829";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BNP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "BNPAHKHP";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("KGI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01610";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "2164";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("ML")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01224";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0161";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("LEHMAN")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "LBFLHKAX";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("LBFLHKAX")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01276";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("NIHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "NSHKHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CCBIS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01813";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CLSA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CLSAHKHH";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else {
			strValue = "";
		}
		return strValue;
	}

	public static String getClearerID(String strBroker, String strMarketShort) {
		String strValue;
		if (strBroker.equalsIgnoreCase("CICC")) {
			strValue = "B01654";
		} else if (strBroker.equalsIgnoreCase("CS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CSFBHKHE";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0443";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01228";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "GSASHKH1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "5208";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("JP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CHASHKK1";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0908";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("UBS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01161";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0642";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01130";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("MS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01274";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0050";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CGMI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01430";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0418";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("RBC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "5002";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GTJA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01565";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CANTOR")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01774";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "7252";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("FSSL")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01686";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01601";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCOM")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01842";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("HTHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01829";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BNP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01299";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("KGI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01610";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "2164";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("ML")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01224";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "0161";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("LEHMAN")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01276";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("LBFLHKAX")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01276";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("NIHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01330";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "2952";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CCBIS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B01813";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "2952";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CLSA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "CITIHKHX";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else {
			strValue = "";
		}
		return strValue;
	}

	public static String getBrokerIDType(String strBroker, String strMarketShort) {
		String strValue = "B";
		if (strMarketShort.equalsIgnoreCase("HK")) {
			if (strBroker.equalsIgnoreCase("FSSL") || strBroker.equalsIgnoreCase("CSC") || strBroker.equalsIgnoreCase("BOCOM") || strBroker.equalsIgnoreCase("HTHK")
					|| strBroker.equalsIgnoreCase("KGI") || strBroker.equalsIgnoreCase("ML") || strBroker.equalsIgnoreCase("CCBIS")) {
				strValue = "H";
			}
		} else if (strMarketShort.equalsIgnoreCase("US")) {
			if (strBroker.equalsIgnoreCase("ML") || strBroker.equalsIgnoreCase("MS") || strBroker.equalsIgnoreCase("CANTOR") || strBroker.equalsIgnoreCase("KGI")) {
				strValue = "Y";
			}
		}
		return strValue;
	}

	public static String getCleareIDType(String strBroker, String strMarketShort) {
		String strValue;
		if (strBroker.equalsIgnoreCase("CICC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("JP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("UBS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("MS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CGMI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("RBC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("GTJA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CANTOR")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("FSSL")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CSC")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BOCOM")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("HTHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("BNP")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("KGI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("ML")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("LEHMAN")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("LBFLHKAX")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "B";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("NIHK")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CCBIS")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CLSA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "H";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "Y";
			} else {
				strValue = "";
			}
		} else {
			strValue = "";
		}
		return strValue;
	}

	public static String getClearerAccount(String strBroker, String strMarketShort) {
		String strValue;
		if (strBroker.equalsIgnoreCase("CICC")) {
			strValue = "";
		} else if (strBroker.equalsIgnoreCase("KGI")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "A/C NO G 68254";
			} else {
				strValue = "";
			}
		} else if (strBroker.equalsIgnoreCase("CLSA")) {
			if (strMarketShort.equalsIgnoreCase("HK")) {
				strValue = "A/C CLSA Ltd";
			} else if (strMarketShort.equalsIgnoreCase("US")) {
				strValue = "";
			} else {
				strValue = "";
			}
		} else {
			strValue = "";
		}
		return strValue;
	}

}
