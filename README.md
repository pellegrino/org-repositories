# Repositories

Display all repositories for your Github organization.

## Running locally

To setup the project locally, this `make build` may come out handy: this will fetch latest leinigen version and use it to fetch all the dependencies this project uses.

Also, in order to run the project you need a few variables set:

* PORT  - The port your project will be running
* SLEEP - Github poll interval, in miliseconds
* URL   - Github API endpoint to fetch your repositories

For more information about these variables, refer to [.env.sample](https://github.com/ohm/org-repositories/blob/master/.env.sample)
