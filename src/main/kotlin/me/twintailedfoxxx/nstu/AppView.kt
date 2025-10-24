package me.twintailedfoxxx.nstu

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class AppView : View("Циклический список"), IModelListener {
    override val root: BorderPane by fxml("/me/twintailedfoxxx/nstu/main-view.fxml")
    
    @FXML lateinit var statusLabel: Label
    @FXML lateinit var sortButton: Button
    @FXML lateinit var removeButton: Button
    @FXML lateinit var insertButton: Button
    @FXML lateinit var addButton: Button
    @FXML lateinit var valueTextField: TextField
    @FXML lateinit var dataListView: ListView<String>
    @FXML lateinit var loadBinaryButton: Button
    @FXML lateinit var saveBinaryButton: Button
    @FXML lateinit var loadJsonButton: Button
    @FXML lateinit var saveJsonButton: Button
    @FXML lateinit var createListButton: Button
    @FXML lateinit var typeComboBox: ComboBox<String>

    private lateinit var model: Model

    override fun onBeforeShow() {
        super.onBeforeShow()

        model = Model()
        model.addListener(this)
        typeComboBox.items.addAll(model.getAvailableTypes())
        updateUiState()
    }

    @FXML
    fun handleCreateList() {
        val selectedType: String? = typeComboBox.value
        if(selectedType == null) {
            showError("Ошибка", "Пожалуйста, выберите тип данных.")
            return
        }

        if(!model.isListCreated()) {
            model.createNewList(selectedType)
            return
        }

        val confirmAlert = Alert(Alert.AlertType.CONFIRMATION)
        confirmAlert.title = "Подтверждение"
        confirmAlert.headerText = "Создание нового списка"
        confirmAlert.contentText = "Текущий список будет заменен. Продолжить?"

        if(confirmAlert.showAndWait().orElse(null) == ButtonType.OK) {
            model.createNewList(selectedType)
        }
    }

    @FXML
    fun handleBinarySave() {
        val chooser =  FileChooser()
        chooser.title = "Сохранить список"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("Binary Files", "*.bin"))
        val file: File? = chooser.showSaveDialog(statusLabel.scene.window)
        if(file != null) {
            try {
                model.saveToBinaryFile(file.absolutePath)
            } catch (e: Exception) {
                showError("Ошибка сохранения", "Не удалось сохранить файл: ${e.message}")
            }
        }
    }

    @FXML
    fun handleBinaryLoad() {
        val chooser = FileChooser()
        chooser.title = "Загрузить список"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("Binary Files", "*.bin"))
        val file: File? = chooser.showOpenDialog(statusLabel.scene.window)
        if(file != null) {
            try {
                model.loadFromBinaryFile(file.absolutePath)
            } catch (e: Exception) {
                showError("Ошибка загрузки", "Не удалось загрузить файл: ${e.message}")
            }
        }
    }

    @FXML
    fun handleJsonSave() {
        val chooser = FileChooser()
        chooser.title = "Сохранить список в JSON"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("JavaScript Object Notation File", "*.json"))
        val file: File? = chooser.showSaveDialog(statusLabel.scene.window)

        if(file != null) {
            try {
                model.saveToJsonFile(file.absolutePath)
            } catch (e: Exception) {
                showError("Ошибка сохранения", "Не удалось сохранить файл: ${e.message}")
            }
        }
    }

    @FXML
    fun handleJsonLoad() {
        val chooser = FileChooser()
        chooser.title = "Загрузить список из JSON"
        chooser.extensionFilters.add(FileChooser.ExtensionFilter("JavaScript Object Notation File", "*.json"))
        val file: File? = chooser.showOpenDialog(statusLabel.scene.window)

        if(file != null) {
            try {
                model.loadFromJsonFile(file.absolutePath)
            } catch (e: Exception) {
                showError("Ошибка загрузки", "Не удалось загрузить файл: ${e.message}")
            }
        }
    }

    @FXML
    fun handleAdd() {
        val value: String? = valueTextField.text
        if(value == null || value.trim().isEmpty()) {
            showError("Ошибка ввода", "Поле значения не может быть пустым.")
            return
        }

        try {
            model.addElement(value)
            valueTextField.clear()
        } catch (e: Exception) {
            showError("Ошибка", "Неверный формат данных: ${e.message}")
        }
    }

    @FXML
    fun handleInsert() {
        val value: String? = valueTextField.text
        val selectedIdx: Int = dataListView.selectionModel.selectedIndex
        if(selectedIdx == -1) {
            return
        }

        if(value == null || value.trim().isEmpty()) {
            showError("Ошибка ввода", "Поле значения не может быть пустым.")
            return
        }

        try {
            model.addElementAt(selectedIdx, value)
            valueTextField.clear()
        } catch (e: Exception) {
            showError("Ошибка", "Неверный формат данных: ${e.message}")
        }
    }

    @FXML
    fun handleRemove() {
        val selectedIdx: Int = dataListView.selectionModel.selectedIndex
        if(selectedIdx == -1) {
            showError("Ошибка", "Выберите элемент для удаления")
            return
        }

        try {
            model.removeElement(selectedIdx)
        } catch (e: Exception) {
            showError("Ошибка", "Не удалось удалить элемент: ${e.message}")
        }
    }

    @FXML
    fun handleSort() {
        val dialog = Alert(Alert.AlertType.CONFIRMATION)
        dialog.title = "Выбор сортировки"
        dialog.headerText = "Выберите стиль сортировки"
        dialog.contentText = "Какой метод сортировки Вы хотите использовать?"

        val imperativeBtn = ButtonType("Императивный метод")
        val functionalBtn = ButtonType("Функциональный метод")

        dialog.buttonTypes.clear()
        dialog.buttonTypes.addAll(imperativeBtn, functionalBtn)

        when(dialog.showAndWait().orElse(null)) {
            imperativeBtn -> {
                model.sortList()
            }
            functionalBtn -> {
                //model.sortListFunctional()
            }
            else -> {}
        }
    }

    override fun onModelChanged() {
        dataListView.items.setAll(model.getElementsList())
        updateUiState()
        statusLabel.text = when {
            (model.isListCreated()) -> "Тип: ${model.getCurrentTypeName()} | Элементов: ${model.getListSize()}"
            else -> "Список не создан. Выберите тип и нажмите \"Создать\"."
        }
    }

    private fun updateUiState() {
        val listCreated: Boolean = model.isListCreated()
        typeComboBox.isDisable = false
        createListButton.isDisable = false
        saveBinaryButton.isDisable = !listCreated
        dataListView.isDisable = !listCreated
        valueTextField.isDisable = !listCreated
        addButton.isDisable = !listCreated
        insertButton.isDisable = !listCreated
        removeButton.isDisable = !listCreated
        sortButton.isDisable = !listCreated
    }

    private fun showError(title: String, message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.contentText = message
        alert.showAndWait()
    }
}