package me.twintailedfoxxx.nstu

import me.twintailedfoxxx.nstu.types.MyInteger
import me.twintailedfoxxx.nstu.types.PolarVector
import me.twintailedfoxxx.nstu.types.IUserType

class UserFactory {
    private val typeList: ArrayList<IUserType> = ArrayList()

    init {
        typeList.add(PolarVector())
        typeList.add(MyInteger())
    }

    fun getTypeNameList() : List<String> {
        val names = ArrayList<String>()

        for(userType: IUserType in typeList) {
            names.add(userType.typeName())
        }

        return names
    }

    fun getBuilderByName(name: String) : IUserType? {
        for(userType: IUserType in typeList) {
            if(userType.typeName() == name) {
                return userType
            }
        }

        return null
    }
}