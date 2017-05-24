angular.module("angular-plugin").directive(
	"includeComponents",
	function(PluginComponentService) {
		return {
			transclude: 'element',
			scope: {
				path : "@"
			},
			link: function(scope, el, attr, ctrl, transclude) {
				var items = PluginComponentService.get(scope.path);
				items.forEach(function(each){
					transclude(function(transEl,transScope) {
						transScope.item = each;
						el.parent().append(transEl);
					});
				});
			}
		}
	});