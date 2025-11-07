package me.twintailedfoxxx.nstu

import com.google.gson.*
import me.twintailedfoxxx.nstu.datastructures.CyclicList
import me.twintailedfoxxx.nstu.types.IUserType
import java.nio.file.Files
import java.nio.file.Paths

class Model {
    private var list: CyclicList<Any>? = null
    private var currentType: IUserType? = null
    private val userFactory: UserFactory = UserFactory()
    private val listeners: ArrayList<IModelListener> = ArrayList()

    fun addListener(listener: IModelListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: IModelListener) {
        listeners.remove(listener)
    }

    fun getAvailableTypes() : List<String> {
        return userFactory.getTypeNameList()
    }

    fun createNewList(typeName: String) {
        currentType = userFactory.getBuilderByName(typeName)
        list = CyclicList()
        notifyListeners()
    }

    fun resetList() {
        list = null
        currentType = null
        notifyListeners()
    }

    fun isListCreated() : Boolean {
        return list != null
    }

    fun addElement(value: String) {
        val list = list ?: throw IllegalArgumentException("List not created")
        val type = currentType ?: throw IllegalArgumentException("No current type")
        val elem = type.parseValue(value)

        list.add(elem)
        notifyListeners()
    }

    fun addElementAt(idx: Int, value: String) {
        val list = list ?: throw IllegalArgumentException("List not created")
        val type = currentType ?: throw IllegalArgumentException("No current type")
        val elem = type.parseValue(value)

        list.add(idx, elem)
        notifyListeners()
    }

    fun removeElement(idx: Int) {
        val list = list ?: throw IllegalArgumentException("List not created")

        list.remove(idx)
        notifyListeners()
    }

    fun sortList() {
        val list = list ?: throw IllegalArgumentException("List not created")
        val type = currentType ?: throw IllegalArgumentException("No current type")

        list.sort(type.comparator())
        notifyListeners()
    }

    fun getElementsList() : List<String> {
        if(list == null) {
            return ArrayList()
        }

        val elements = ArrayList<String>()
        for(i in 0..<list!!.size()) {
            elements.add(list!!.get(i).toString())
        }

        return elements
    }

    fun getListSize() : Int {
        return when {
            (list != null) -> list!!.size()
            else -> 0
        }
    }

    fun saveToBinaryFile(fileName: String) {
        val list = list ?: throw IllegalArgumentException("List not created")
        list.serialize(fileName)
    }

    fun loadFromBinaryFile(fileName: String) {
        try {
            list = CyclicList.deserialize(fileName)
            if (list!!.size() > 0) {
                val firstElem = list!!.get(0)
                currentType = userFactory.getTypeNameList()
                    .firstNotNullOfOrNull { typeName -> 
                        userFactory.getBuilderByName(typeName)?.takeIf { it.javaClass.isInstance(firstElem) }
                    }
                    ?: throw IllegalStateException("Unknown type for deserialized data")
            } else {
                currentType = null
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to load file: ${e.message}", e)
        }
        notifyListeners()
    }

    fun saveToJsonFile(fileName: String) {
        requireNotNull(list) { "List not created" }

        val jsonObject = JsonObject()
        jsonObject.addProperty("typeName", currentType!!.typeName())

        val jsonArray = JsonArray()
        list!!.forEach {
            jsonArray.add(Gson().toJsonTree(it))
        }
        jsonObject.add("elements", jsonArray)

        val json = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(jsonObject)

        Files.write(Paths.get(fileName), json.toByteArray())
    }

    fun loadFromJsonFile(fileName: String) {
        val json = Files.readString(Paths.get(fileName))
        val jsonObject = JsonParser.parseString(json).asJsonObject
        val typeName = jsonObject.get("typeName").asString

        currentType = userFactory.getBuilderByName(typeName)
        if(currentType == null) {
            throw IllegalStateException("Unknown type: $typeName")
        }

        list = CyclicList()
        val jsonArray = jsonObject.getAsJsonArray("elements")

        jsonArray.forEach {
            list!!.add(currentType!!.parseFromJson(it.toString()))
        }

        notifyListeners()
    }

    fun getCurrentTypeName() : String? {
        return when {
            (currentType != null) -> currentType!!.typeName()
            else -> null
        }
    }

    private fun notifyListeners() {
        for(listener: IModelListener in listeners) {
            listener.onModelChanged()
        }
    }
}

interface IModelListener {
    fun onModelChanged()
}