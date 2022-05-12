package fr.dut.typetrainer.type
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import me.sargunvohra.lib.pokekotlin.model.NamedApiResource
import me.sargunvohra.lib.pokekotlin.model.Type

class TypeManager(context: Context) {
    private val typeList = mutableListOf<Type>()
    private var loading = true
    var oddsOfDoubleType = 0.35
    init {
        val sharedPreferences = context.getSharedPreferences("type", Context.MODE_PRIVATE) //save the type list in cache
        val typeListString = sharedPreferences.getString("typeList", "")
        val gson = Gson()
        if (typeListString != "") {
            Log.d("TypeManager", "Loading type list from shared preferences")
            Log.d("TypeManager","Type list: $typeListString")
            val typeListJson = gson.fromJson(typeListString, Array<Type>::class.java)
            typeList.addAll(typeListJson)
            loading = false
        }
        else{
            Log.d("TypeManager", "Loading type list from PokeAPI")
            CoroutineScope(Dispatchers.IO).launch {
                fillTypeList()
                sharedPreferences.edit().putString("typeList", gson.toJson(typeList)).apply()
            }
        }
    }

    fun isLoading() = loading
    private fun fillTypeList() {
        val pokeApiClient = PokeApiClient()
        val typeNameList = pokeApiClient.getTypeList(0, 18)
        for (type in typeNameList.results) {
            typeList.add(pokeApiClient.getType(type.id))
        }
        loading = false
    }

    fun getRandomType(): Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        return typeList.random()
    }

    private fun getDifferentRandomType(types: List<Type>): Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        val typeListCopy = typeList.toMutableList()
        typeListCopy.removeAll(types)
        if (typeListCopy.isEmpty()) {
            throw IllegalStateException("No more types available")
        }
        return typeListCopy.random()
    }

    fun getTypeCombination(): Array<Type?> {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        val randomDouble = Math.random() // Odds of getting 2 types
        val type1 = getRandomType()
        var type2 : Type? = null
        if (randomDouble > oddsOfDoubleType) {
            type2 = getDifferentRandomType(listOf(type1))
        }

        return arrayOf(type1, type2)
    }

    private fun getEfficiency(typeAtq: Type, typeDef: Type): Double {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        if (containsType(typeAtq.damageRelations.doubleDamageTo, typeDef)) {
            return 2.0
        }
        if (containsType(typeAtq.damageRelations.halfDamageTo, typeDef)) {
            return 0.5
        }
        if (containsType(typeAtq.damageRelations.noDamageTo, typeDef)) {
            return 0.0
        }
        return 1.0
    }

    private fun containsType(list: List<NamedApiResource>, type: Type): Boolean {
        for (namedApiResource in list) {
            if (namedApiResource.name == type.name) {
                return true
            }
        }
        return false
    }
    /*fun getType(id:Int): Type{
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        for (type in typeList){
            if (type.id == id){
                return type
            }
        }
        throw IllegalStateException("Type not found")
    }*/
    /*fun getMostEffectiveDefenseType(typeAtq: Type): Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        var maxEfficiency = 0.0
        var maxEfficiencyType: Type? = null
        for (typeDef in typeList) {
            val efficiency = getEfficiency(typeAtq, typeDef)
            if (efficiency > maxEfficiency) {
                maxEfficiency = efficiency
                maxEfficiencyType = typeDef
                if (maxEfficiency == 2.0) {
                    break
                }
            }
        }
        return maxEfficiencyType!!
    }*/

    /*fun getMostEffectiveAttackType(typeDef:Type): Type{
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        var maxEfficiency = 0.0
        var mostEffectiveAttackType: Type? = null
        for (typeAtq in typeList){
            val efficiency = getEfficiency(typeAtq, typeDef)
            if (efficiency > maxEfficiency){
                maxEfficiency = efficiency
                mostEffectiveAttackType = typeAtq
                if (maxEfficiency == 2.0){
                    break
                }
            }
        }
        if (mostEffectiveAttackType == null){
            throw IllegalStateException("No more types available")
        }
        return mostEffectiveAttackType
    }*/
    /*fun getMostEffectiveAttackType(typeDef1:Type,typeDef2:Type): Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        val neutralDef1 = getNeutralTypes(typeDef1)
        val neutralDef2 = getNeutralTypes(typeDef2)
        val superDef1 = getDoubleDamageType(typeDef1)
        val superDef2 = getDoubleDamageType(typeDef2)
        val halfDef1 = getHalfDamageType(typeDef1)
        val halfDef2 = getHalfDamageType(typeDef2)
        val noDef1 = getNoDamageType(typeDef1)
        val noDef2 = getNoDamageType(typeDef2)

        //Super effective 4
        val super4 = superDef1.intersect(superDef2)
        if (super4.isNotEmpty()) {
            return super4.random()
        }

        //Super effective 2
        val super2 = superDef1.intersect(neutralDef2).union(superDef2.intersect(neutralDef1))
        if (super2.isNotEmpty()) {
            return super2.random()
        }
        //Neutral
        val neutral = neutralDef1.intersect(neutralDef2).union(superDef1.intersect(halfDef2)).union(halfDef1.intersect(superDef2))
        if (neutral.isNotEmpty()) {
            return neutral.random()
        }

        //Half effective
        val half2 = halfDef1.intersect(neutralDef1).union(halfDef2.intersect(neutralDef2))
        if (half2.isNotEmpty()) {
            return half2.random()
        }

        //Quarter effective
        val half4 = halfDef1.intersect(superDef2)
        if (half4.isNotEmpty()) {
            return half4.random()
        }

        //No effective
        val no = noDef1.union(noDef2)
        if (no.isNotEmpty()) {
            return no.random()
        }
        throw IllegalStateException("No type found")
    }*/

    /*private fun getNoDamageType(typeDef1: Type): Set<Type> {
        val noDamageTypes = mutableSetOf<Type>()
        for (type in typeList) {
            if (containsType(type.damageRelations.noDamageTo, typeDef1)) {
                noDamageTypes.add(type)
            }
        }
        return noDamageTypes
    }*/

    /*private fun getHalfDamageType(typeDef1: Type): Set<Type> {
        val halfDamageTypes = mutableSetOf<Type>()
        for (type in typeList) {
            if (containsType(type.damageRelations.halfDamageTo, typeDef1)) {
                halfDamageTypes.add(type)
            }
        }
        return halfDamageTypes
    }*/

    /*private fun getDoubleDamageType(typeDef1: Type): Set<Type> {
        val doubleDamageTypes = mutableSetOf<Type>()
        for (type in typeList) {
            if (containsType(type.damageRelations.doubleDamageFrom, typeDef1)) {
                doubleDamageTypes.add(type)
            }
        }
        return doubleDamageTypes
    }*/

    /*private fun getNeutralTypes(typeDef: Type): Set<Type> {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        val superEffective = typeDef.damageRelations.doubleDamageFrom
        val notVeryEffective = typeDef.damageRelations.halfDamageFrom
        val noEffect = typeDef.damageRelations.noDamageFrom
        val neutralType = HashSet<Type>()
        typeList.forEach{
            if ( !containsType(superEffective, it) && !containsType(notVeryEffective, it) && !containsType(noEffect, it)){
                neutralType.add(it)
            }
        }
        return neutralType
    }*/

    fun getEfficiency(typeAtq: Type, typeDef1: Type, typeDef: Type?): Double {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        val eff1 = getEfficiency(typeAtq, typeDef1)
        if (typeDef == null){
            return eff1
        }
        val eff2 = getEfficiency(typeAtq, typeDef)
        Log.d("TypeManager", "Efficiency of ${typeAtq.name} against ${typeDef1.name} is $eff1")
        Log.d("TypeManager", "Efficiency of ${typeAtq.name} against ${typeDef.name} is $eff2")
        Log.d("TypeManager", "Efficiency of ${typeAtq.name} against ${typeDef1.name} and ${typeDef.name} is ${eff1 * eff2}")
        return eff1 * eff2
    }

    /*fun getType(name: String): Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        for (type in typeList) {
            if (type.name == name) {
                return type
            }
        }
        throw IllegalArgumentException("Type $name not found")
    }*/
}