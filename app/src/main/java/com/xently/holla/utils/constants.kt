package com.xently.holla.utils

import android.content.Context
import android.telephony.TelephonyManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.intellij.lang.annotations.Language
import java.text.DateFormat
import java.util.*

val JSON_CONVERTER: Gson = GsonBuilder()
//        .registerTypeAdapter(Id::class.java, IdTypeAdapter())
    .enableComplexMapKeySerialization()
    .serializeNulls()
    .setDateFormat(DateFormat.LONG)
    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
    .setPrettyPrinting()
//        .setVersion(1.0)
    .create()

inline fun <reified T> objectFromJson(json: String?): T? = if (json.isNullOrBlank()) null else try {
    JSON_CONVERTER.fromJson(json, T::class.java)
} catch (ex: Exception) {
    null
}

interface IData<T> {
    fun fromJson(@Language("JSON") json: String?): T?

    fun fromMap(map: Map<String, Any?>): T? = fromJson(JSON_CONVERTER.toJson(map))
}

object CountryISOInitialsToCode {
    /**
     * Gets country (phone) code for country with ISO initials = [isoInitial]
     * @param isoInitial Country's ISO initial e.g. KE for Kenya
     * @return Country/phone code for country with initial = [isoInitial] e.g. if [isoInitial] = KE,
     * returned value = +254
     */
    fun getCountryCode(isoInitial: String?): String? {
        return if (isoInitial == null) null
        else countryInitialsToCode[isoInitial.toUpperCase(Locale.ROOT)]
    }

    fun getCountryCode(context: Context): String? {
        var iso: String? = null
        val telephonyManager =
            context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkCountryIso = telephonyManager.networkCountryIso
        if (networkCountryIso != null && networkCountryIso.toString() != "")
            iso = networkCountryIso.toString()
        return getCountryCode("KE") // TODO: Replace with retrieved
    }

    /**
     * K(Key) = Country ISO initials e.g. KE for Kenya
     * V(Value) = Country Code e.g. +254 for Kenya
     */
    val all: Map<String, String>
        get() = countryInitialsToCode

    private val countryInitialsToCode: MutableMap<String, String> = HashMap()

    init {
        countryInitialsToCode["AF"] = "+93"
        countryInitialsToCode["AL"] = "+355"
        countryInitialsToCode["DZ"] = "+213"
        countryInitialsToCode["AD"] = "+376"
        countryInitialsToCode["AO"] = "+244"
        countryInitialsToCode["AG"] = "+1-268"
        countryInitialsToCode["AR"] = "+54"
        countryInitialsToCode["AM"] = "+374"
        countryInitialsToCode["AU"] = "+61"
        countryInitialsToCode["AT"] = "+43"
        countryInitialsToCode["AZ"] = "+994"
        countryInitialsToCode["BS"] = "+1-242"
        countryInitialsToCode["BH"] = "+973"
        countryInitialsToCode["BD"] = "+880"
        countryInitialsToCode["BB"] = "+1-246"
        countryInitialsToCode["BY"] = "+375"
        countryInitialsToCode["BE"] = "+32"
        countryInitialsToCode["BZ"] = "+501"
        countryInitialsToCode["BJ"] = "+229"
        countryInitialsToCode["BT"] = "+975"
        countryInitialsToCode["BO"] = "+591"
        countryInitialsToCode["BA"] = "+387"
        countryInitialsToCode["BW"] = "+267"
        countryInitialsToCode["BR"] = "+55"
        countryInitialsToCode["BN"] = "+673"
        countryInitialsToCode["BG"] = "+359"
        countryInitialsToCode["BF"] = "+226"
        countryInitialsToCode["BI"] = "+257"
        countryInitialsToCode["KH"] = "+855"
        countryInitialsToCode["CM"] = "+237"
        countryInitialsToCode["CA"] = "+1"
        countryInitialsToCode["CV"] = "+238"
        countryInitialsToCode["CF"] = "+236"
        countryInitialsToCode["TD"] = "+235"
        countryInitialsToCode["CL"] = "+56"
        countryInitialsToCode["CN"] = "+86"
        countryInitialsToCode["CO"] = "+57"
        countryInitialsToCode["KM"] = "+269"
        countryInitialsToCode["CD"] = "+243"
        countryInitialsToCode["CG"] = "+242"
        countryInitialsToCode["CR"] = "+506"
        countryInitialsToCode["CI"] = "+225"
        countryInitialsToCode["HR"] = "+385"
        countryInitialsToCode["CU"] = "+53"
        countryInitialsToCode["CY"] = "+357"
        countryInitialsToCode["CZ"] = "+420"
        countryInitialsToCode["DK"] = "+45"
        countryInitialsToCode["DJ"] = "+253"
        countryInitialsToCode["DM"] = "+1-767"
        countryInitialsToCode["DO"] = "+1-809and1-829"
        countryInitialsToCode["EC"] = "+593"
        countryInitialsToCode["EG"] = "+20"
        countryInitialsToCode["SV"] = "+503"
        countryInitialsToCode["GQ"] = "+240"
        countryInitialsToCode["ER"] = "+291"
        countryInitialsToCode["EE"] = "+372"
        countryInitialsToCode["ET"] = "+251"
        countryInitialsToCode["FJ"] = "+679"
        countryInitialsToCode["FI"] = "+358"
        countryInitialsToCode["FR"] = "+33"
        countryInitialsToCode["GA"] = "+241"
        countryInitialsToCode["GM"] = "+220"
        countryInitialsToCode["GE"] = "+995"
        countryInitialsToCode["DE"] = "+49"
        countryInitialsToCode["GH"] = "+233"
        countryInitialsToCode["GR"] = "+30"
        countryInitialsToCode["GD"] = "+1-473"
        countryInitialsToCode["GT"] = "+502"
        countryInitialsToCode["GN"] = "+224"
        countryInitialsToCode["GW"] = "+245"
        countryInitialsToCode["GY"] = "+592"
        countryInitialsToCode["HT"] = "+509"
        countryInitialsToCode["HN"] = "+504"
        countryInitialsToCode["HU"] = "+36"
        countryInitialsToCode["IS"] = "+354"
        countryInitialsToCode["IN"] = "+91"
        countryInitialsToCode["ID"] = "+62"
        countryInitialsToCode["IR"] = "+98"
        countryInitialsToCode["IQ"] = "+964"
        countryInitialsToCode["IE"] = "+353"
        countryInitialsToCode["IL"] = "+972"
        countryInitialsToCode["IT"] = "+39"
        countryInitialsToCode["JM"] = "+1-876"
        countryInitialsToCode["JP"] = "+81"
        countryInitialsToCode["JO"] = "+962"
        countryInitialsToCode["KZ"] = "+7"
        countryInitialsToCode["KE"] = "+254"
        countryInitialsToCode["KI"] = "+686"
        countryInitialsToCode["KP"] = "+850"
        countryInitialsToCode["KR"] = "+82"
        countryInitialsToCode["KW"] = "+965"
        countryInitialsToCode["KG"] = "+996"
        countryInitialsToCode["LA"] = "+856"
        countryInitialsToCode["LV"] = "+371"
        countryInitialsToCode["LB"] = "+961"
        countryInitialsToCode["LS"] = "+266"
        countryInitialsToCode["LR"] = "+231"
        countryInitialsToCode["LY"] = "+218"
        countryInitialsToCode["LI"] = "+423"
        countryInitialsToCode["LT"] = "+370"
        countryInitialsToCode["LU"] = "+352"
        countryInitialsToCode["MK"] = "+389"
        countryInitialsToCode["MG"] = "+261"
        countryInitialsToCode["MW"] = "+265"
        countryInitialsToCode["MY"] = "+60"
        countryInitialsToCode["MV"] = "+960"
        countryInitialsToCode["ML"] = "+223"
        countryInitialsToCode["MT"] = "+356"
        countryInitialsToCode["MH"] = "+692"
        countryInitialsToCode["MR"] = "+222"
        countryInitialsToCode["MU"] = "+230"
        countryInitialsToCode["MX"] = "+52"
        countryInitialsToCode["FM"] = "+691"
        countryInitialsToCode["MD"] = "+373"
        countryInitialsToCode["MC"] = "+377"
        countryInitialsToCode["MN"] = "+976"
        countryInitialsToCode["ME"] = "+382"
        countryInitialsToCode["MA"] = "+212"
        countryInitialsToCode["MZ"] = "+258"
        countryInitialsToCode["MM"] = "+95"
        countryInitialsToCode["NA"] = "+264"
        countryInitialsToCode["NR"] = "+674"
        countryInitialsToCode["NP"] = "+977"
        countryInitialsToCode["NL"] = "+31"
        countryInitialsToCode["NZ"] = "+64"
        countryInitialsToCode["NI"] = "+505"
        countryInitialsToCode["NE"] = "+227"
        countryInitialsToCode["NG"] = "+234"
        countryInitialsToCode["NO"] = "+47"
        countryInitialsToCode["OM"] = "+968"
        countryInitialsToCode["PK"] = "+92"
        countryInitialsToCode["PW"] = "+680"
        countryInitialsToCode["PA"] = "+507"
        countryInitialsToCode["PG"] = "+675"
        countryInitialsToCode["PY"] = "+595"
        countryInitialsToCode["PE"] = "+51"
        countryInitialsToCode["PH"] = "+63"
        countryInitialsToCode["PL"] = "+48"
        countryInitialsToCode["PT"] = "+351"
        countryInitialsToCode["QA"] = "+974"
        countryInitialsToCode["RO"] = "+40"
        countryInitialsToCode["RU"] = "+7"
        countryInitialsToCode["RW"] = "+250"
        countryInitialsToCode["KN"] = "+1-869"
        countryInitialsToCode["LC"] = "+1-758"
        countryInitialsToCode["VC"] = "+1-784"
        countryInitialsToCode["WS"] = "+685"
        countryInitialsToCode["SM"] = "+378"
        countryInitialsToCode["ST"] = "+239"
        countryInitialsToCode["SA"] = "+966"
        countryInitialsToCode["SN"] = "+221"
        countryInitialsToCode["RS"] = "+381"
        countryInitialsToCode["SC"] = "+248"
        countryInitialsToCode["SL"] = "+232"
        countryInitialsToCode["SG"] = "+65"
        countryInitialsToCode["SK"] = "+421"
        countryInitialsToCode["SI"] = "+386"
        countryInitialsToCode["SB"] = "+677"
        countryInitialsToCode["SO"] = "+252"
        countryInitialsToCode["ZA"] = "+27"
        countryInitialsToCode["ES"] = "+34"
        countryInitialsToCode["LK"] = "+94"
        countryInitialsToCode["SD"] = "+249"
        countryInitialsToCode["SR"] = "+597"
        countryInitialsToCode["SZ"] = "+268"
        countryInitialsToCode["SE"] = "+46"
        countryInitialsToCode["CH"] = "+41"
        countryInitialsToCode["SY"] = "+963"
        countryInitialsToCode["TJ"] = "+992"
        countryInitialsToCode["TZ"] = "+255"
        countryInitialsToCode["TH"] = "+66"
        countryInitialsToCode["TL"] = "+670"
        countryInitialsToCode["TG"] = "+228"
        countryInitialsToCode["TO"] = "+676"
        countryInitialsToCode["TT"] = "+1-868"
        countryInitialsToCode["TN"] = "+216"
        countryInitialsToCode["TR"] = "+90"
        countryInitialsToCode["TM"] = "+993"
        countryInitialsToCode["TV"] = "+688"
        countryInitialsToCode["UG"] = "+256"
        countryInitialsToCode["UA"] = "+380"
        countryInitialsToCode["AE"] = "+971"
        countryInitialsToCode["GB"] = "+44"
        countryInitialsToCode["US"] = "+1"
        countryInitialsToCode["UY"] = "+598"
        countryInitialsToCode["UZ"] = "+998"
        countryInitialsToCode["VU"] = "+678"
        countryInitialsToCode["VA"] = "+379"
        countryInitialsToCode["VE"] = "+58"
        countryInitialsToCode["VN"] = "+84"
        countryInitialsToCode["YE"] = "+967"
        countryInitialsToCode["ZM"] = "+260"
        countryInitialsToCode["ZW"] = "+263"
        countryInitialsToCode["GE"] = "+995"
        countryInitialsToCode["TW"] = "+886"
        countryInitialsToCode["AZ"] = "+374-97"
        countryInitialsToCode["CY"] = "+90-392"
        countryInitialsToCode["MD"] = "+373-533"
        countryInitialsToCode["SO"] = "+252"
        countryInitialsToCode["GE"] = "+995"
        countryInitialsToCode["CX"] = "+61"
        countryInitialsToCode["CC"] = "+61"
        countryInitialsToCode["NF"] = "+672"
        countryInitialsToCode["NC"] = "+687"
        countryInitialsToCode["PF"] = "+689"
        countryInitialsToCode["YT"] = "+262"
        countryInitialsToCode["GP"] = "+590"
        countryInitialsToCode["GP"] = "+590"
        countryInitialsToCode["PM"] = "+508"
        countryInitialsToCode["WF"] = "+681"
        countryInitialsToCode["CK"] = "+682"
        countryInitialsToCode["NU"] = "+683"
        countryInitialsToCode["TK"] = "+690"
        countryInitialsToCode["GG"] = "+44"
        countryInitialsToCode["IM"] = "+44"
        countryInitialsToCode["JE"] = "+44"
        countryInitialsToCode["AI"] = "+1-264"
        countryInitialsToCode["BM"] = "+1-441"
        countryInitialsToCode["IO"] = "+246"
        countryInitialsToCode[""] = "+357"
        countryInitialsToCode["VG"] = "+1-284"
        countryInitialsToCode["KY"] = "+1-345"
        countryInitialsToCode["FK"] = "+500"
        countryInitialsToCode["GI"] = "+350"
        countryInitialsToCode["MS"] = "+1-664"
        countryInitialsToCode["SH"] = "+290"
        countryInitialsToCode["TC"] = "+1-649"
        countryInitialsToCode["MP"] = "+1-670"
        countryInitialsToCode["PR"] = "+1-787and1-939"
        countryInitialsToCode["AS"] = "+1-684"
        countryInitialsToCode["GU"] = "+1-671"
        countryInitialsToCode["VI"] = "+1-340"
        countryInitialsToCode["HK"] = "+852"
        countryInitialsToCode["MO"] = "+853"
        countryInitialsToCode["FO"] = "+298"
        countryInitialsToCode["GL"] = "+299"
        countryInitialsToCode["GF"] = "+594"
        countryInitialsToCode["GP"] = "+590"
        countryInitialsToCode["MQ"] = "+596"
        countryInitialsToCode["RE"] = "+262"
        countryInitialsToCode["AX"] = "+358-18"
        countryInitialsToCode["AW"] = "+297"
        countryInitialsToCode["AN"] = "+599"
        countryInitialsToCode["SJ"] = "+47"
        countryInitialsToCode["AC"] = "+247"
        countryInitialsToCode["TA"] = "+290"
        countryInitialsToCode["CS"] = "+381"
        countryInitialsToCode["PS"] = "+970"
        countryInitialsToCode["EH"] = "+212"
    }
}