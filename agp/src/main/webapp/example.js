//the ajaxUrl variable was drawn by the jsp file on top
// this hidden input was also drawn by the jsp
const csrfToken = document.getElementsByName('csrftoken')[0].value

const headers = new Headers(
    {
        'Content-Type': 'application/json',
        'csrf-token': document.getElementsByName('csrftoken')[0].value,
    }
)

const json = () => callApi("test")
const redirect = () => callApi("redirect")
const file = () => callApi("file")

const callApi = (arg) => fetch(ajaxUrl, {
    method: "POST",
    body: JSON.stringify({
        arg,
    }),
    headers
}).then(response => {
    if (response.headers.get('Content-Type').startsWith("application/json")) {
        return response.json().then(json => {
            checkRedirect(json)
            console.log("Server ajax response:")
            console.log(json)
        })
    }
    else {
        downloadFile(response);
    }
})
const checkRedirect = (response) => {
    if ("REDIRECT_URL" in response) {
        window.location.href = response["REDIRECT_URL"]
    }
}

const downloadFile = async (response) => {
    const blob = await response.blob()
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
    var url = window.URL.createObjectURL(blob);
    a.href = url;
    a.download = "file.txt";
    a.click();
    window.URL.revokeObjectURL(url);
}
