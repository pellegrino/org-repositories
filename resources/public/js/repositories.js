$(function () {

  var Repository = Backbone.Model.extend({});

  var RepositoryView = Backbone.View.extend({
    tagName: "tr",

    className: function () {
      return this.model.get('private') === true ? 'private' : 'public';
    },

    template: _.template($("#repository-view").html()),

    render: function () {
      $(this.el).html(this.template(this.model.toJSON()));
      return this;
    }
  });

  var RepositoryList = Backbone.Collection.extend({
    model: Repository,

    url: "/repositories.json",

    comparator: function (repository) {
      return repository.get('name');
    }
  });

  var RepositoryListView = Backbone.View.extend({
    el: $("#repositories"),

    initialize: function () {
      this.collection = new RepositoryList;
      this.listenTo(this.collection, 'reset', this.render);
      this.collection.fetch({reset: true});
    },

    render: function() {
      var el = $(this.el);
      el.empty();
      this.collection.each(function (repository) {
        var view = new RepositoryView({model: repository});
        el.append(view.render().el);
      });
    }
  });

  new RepositoryListView;

});
