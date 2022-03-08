package fr.dut.typetrainer.type

import android.media.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient
import me.sargunvohra.lib.pokekotlin.model.NamedApiResource
import me.sargunvohra.lib.pokekotlin.model.Type
import java.util.*

class TypeManager {
    private val typeList = mutableListOf<Type>()
    private var loading = true
    init {
        CoroutineScope(Dispatchers.IO).launch{
            fillTypeList()
        }
    }
    private fun fillTypeList() {
        val pokeApiClient = PokeApiClient()
        val typeNameList = pokeApiClient.getTypeList(0,18)
        for (type in typeNameList.results) {
            typeList.add(pokeApiClient.getType(type.id))
        }
        loading = false
    }
    fun getRandomType() : Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        return typeList.random()
    }
    private fun getDifferentRandomType(types: List<Type>) : Type {
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
    fun getTypeCombination() : Array<Type> {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        val type1 = getRandomType()
        val type2 = getDifferentRandomType(listOf(type1))
        return arrayOf(type1,type2)
    }
    fun getEfficiency(typeAtq: Type, typeDef: Type) : Double {
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
    private fun containsType(list : List<NamedApiResource>,type:Type) : Boolean {
        for (namedApiResource in list) {
            if (namedApiResource.name == type.name) {
                return true
            }
        }
        return false
    }
    fun getEfficiency(typeAtq: Type, typeDef1:  Type, typeDef: Type) : Double{
        return getEfficiency(typeAtq,typeDef) * getEfficiency(typeDef1,typeDef)
    }

    fun getType(name: String) : Type {
        if (loading) {
            throw IllegalStateException("Type list is loading")
        }
        for (type in typeList) {
            if (type.name == name) {
                return type
            }
        }
        throw IllegalArgumentException("Type $name not found")
    }
}