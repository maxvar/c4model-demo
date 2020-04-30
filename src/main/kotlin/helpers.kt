import com.structurizr.model.Container
import com.structurizr.model.DeploymentNode
import com.structurizr.model.Relationship
import com.structurizr.model.SoftwareSystem

fun Relationship?.withUrl(url: String): Relationship? {
    this?.url = url
    return this
}

fun Relationship?.withProperty(key: String, value: String): Relationship? {
    this?.addProperty(key, value)
    return this
}

fun Relationship?.withTags(vararg tags: String): Relationship? {
    this?.addTags(*tags)
    return this
}

fun SoftwareSystem.withUrl(url: String): SoftwareSystem {
    this.url = url
    return this
}

fun SoftwareSystem.withTags(vararg tags: String): SoftwareSystem {
    this.addTags(*tags)
    return this
}

fun Container.withUrl(url: String): Container {
    this.url = url
    return this
}

fun Container.withTags(vararg tags: String): Container {
    this.addTags(*tags)
    return this
}

fun DeploymentNode.add(vararg container: Container) = container.forEach { this.add(it) }
