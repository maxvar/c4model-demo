import com.structurizr.Workspace
import com.structurizr.api.StructurizrClient
import com.structurizr.model.*
import com.structurizr.model.Location.External
import com.structurizr.model.Location.Internal
import com.structurizr.view.AutomaticLayout
import com.structurizr.view.Shape

const val DATABASE_TAG = "Database"
const val API_URL = "http://localhost:8080/api"
const val WORKSPACE_ID = 2L
const val API_KEY = "1ba6cf7e-adbb-4a4d-9e01-bc6b1b897364"
const val SECRET = "ec08f4c3-a3f2-41fa-a9ba-52833e255f68"

fun main() {
    //model and properties
    val workspace = Workspace("Demo workspace", "This model shows how it all works (or doesn't)")
    val model = workspace.model

    //main contents
    fillModel(model, workspace)

    //some styling
    workspace.views.configuration.styles.apply {
        addElementStyle(Tags.ELEMENT).background("#438dd5").color("#ffffff")
        addElementStyle(Tags.PERSON).background("#08427b").shape(Shape.Person)
        addElementStyle(Tags.CONTAINER).background("#438dd5")
        addElementStyle(DATABASE_TAG).shape(Shape.Cylinder)
    }

    //publish
    StructurizrClient(API_URL, API_KEY, SECRET).putWorkspace(WORKSPACE_ID, workspace)
}

private fun fillModel(model: Model, workspace: Workspace) {
    model.apply {
        enterprise = Enterprise("DEMO enterprise")

        val theSystem = addSoftwareSystem(Internal, "Our system", "Provides something useful")
        val dependency = addSoftwareSystem(External, "Useful dependency", "Provides data for our system")
            .withUrl("https://dependency-doc.net")
        val dependency_entrypoint = dependency.addContainer("Dependency Entry", "Integration point", "Unknown")

        theSystem.uses(dependency, "Calls for fresh data")
        val user = addPerson(External, "User", "Dear user")
        user.uses(theSystem, "Does some valuable stuff")
            .withUrl("https://admin-theSystem-usage.net")

        val admin = addPerson(Internal, "Admin", "Unknown hero")
        admin.uses(theSystem, "Manages the system")
        val apiClient = addSoftwareSystem(External, "API client", "External partner's system")
            .withUrl("https://apiClient-doc.net")
        apiClient.uses(theSystem, "Uses system's services")

        theSystem.apply {
            val frontend = addContainer("Web app", "Web UI application", "Frontend magic")
            val backend = addContainer("Backend app", "Internal application", "Backend magic")
            val apiServer = addContainer("API gateway", "Public API application", "API gate")
            val datastore = addContainer("DB", "Persists all own data", "SomeDb")
                .withTags(DATABASE_TAG)
            val runner = addContainer("Tasks runner", "Does some usefull staff", "Scheduler")

            user.uses(frontend, "Uses UI to do stuff", "https")
                .withUrl("https://user-frontend-usage.net")
            apiClient.uses(apiServer, "Calls services", "https/json")
                .withUrl("https://apiClient-apiServer-usage.net")
            admin.uses(frontend, "Administers users", "https")
                .withUrl("https://admin-frontend-usage.net")
            frontend.uses(backend, "Invokes business logic have work done")
            apiServer.uses(backend, "Invokes business logic have work done")
            backend.uses(datastore, "Saves and retrieves data", "dbc")
                .withUrl("https://backend-datastore-usage.net")
            backend.uses(runner, "Schedules data updates", "https")
            runner.uses(dependency_entrypoint, "Retrieves useful data", "https")
            runner.uses(backend, "Delivers requested data", "https")
                .withUrl("https://runner-backend-usage.net")

            val extdepnode = addDeploymentNode("enternets.com", "external dep's DC", "Unknown")
            extdepnode.add(dependency_entrypoint)

            val bigFuckingServer = addDeploymentNode("big server 10.0.0.15", "appsrv", "linux")
            bigFuckingServer.add(datastore)

            val appcluster = addDeploymentNode("cluster 10.0.0.18", "virtual cluster", "linux")
            val appsrv1 = appcluster.addDeploymentNode("app server 1 10.0.0.16", "tomcat server", "linux")
            val appsrv2 = appcluster.addDeploymentNode("app server 2 10.0.0.17", "tomcat server", "linux")
            appsrv1.add(apiServer, backend, frontend, runner)
            appsrv2.add(apiServer, frontend)


        }

        model.elements.forEach {
            if (it.url != null) println("${it.name} has url ${it.url}")
        }

        workspace.views.createSystemContextView(
            theSystem, "Main diagram",
            "Describes The System in its environment"
        ).apply {
            addAllElements()
            enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 200, 100, 100, false)
        }

        workspace.views.createContainerView(
            theSystem, "The Systems internals",
            "Describes the system internal structure and interactions"
        ).apply {
            addAllElements()
//            enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 200, 100, 100, false)
        }

        workspace.views.createDeploymentView("Current", "description")
            .apply {
                addAllDeploymentNodes()
//                enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 200, 100, 100, false)
            }

    }
}
