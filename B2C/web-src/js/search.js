
define('Search', ['Element', 'Elements', 'Ajax', 'FormData', 'Template', 'CartBtn'],
    function ($, $$, $http, $form, $tmpl, $cartbtn) {
        'use strict';

        if (!$('.search-filters').elem()) {return;}

        var searchUri     = ContextPath + '/browse?'
          , searchTabs    = $$('.search-filters .nav-tabs li a')
          , searchMore    = $('.search-filters .nav-tabs li a[data-id="more"]').parent()
          , searchOpts    = $('.search-options')
          , searchTerm    = $('input[name="searchTerm"]')
          , category      = $('select[name="catId"]')
          , orderBy       = $('select[name="orderBy"]')
          , minPrice      = $('input[name="minPrice"]')
          , maxPrice      = $('input[name="maxPrice"]')
          , minPriceLabel = $('input[name="minPrice"] + span')
          , maxPriceLabel = $('input[name="maxPrice"] + span')
          , closeBtn      = $('.search-options .close-btn .btn')
          , submit        = $('.search-box button[type="submit"]')
          , searchForm    = $('form[role="search"]')
          , searchResults = $('.catalog')
          ;

        function toggleAdvancedSearch(toggle) {
            if (!toggle || searchMore.hasClass('active')) {
                searchMore.removeClass('active');
                searchOpts.addClass('hidden');
                if (toggle) {
                    searchTabs.each(function (el) {
                        if (el.attr('data-id') === category.val()) {
                            el.parent().addClass('active');
                        }
                    });
                }
            } else if (toggle) {
                searchMore.addClass('active');
                searchOpts.removeClass('hidden');
            }
        }

        function showError(error) {
            $('.error')
                .html(error)
                .addClass('alert')
                .addClass('alert-danger')
                .removeClass('hidden');
        }

        function updateSearchResults() {
            var form = $form(searchForm.elem());
            var uri  = searchUri + form.encode();
            var ref  = encodeURIComponent(uri);
            window.history.pushState(null, $('title').html(), uri);
            $('.signin').attr('href', ContextPath + '/api/login?ref=' + ref);
            $('.signout').attr('href', ContextPath + '/api/login?action=logout&ref=' + ref);
            $http
                .get(searchUri + form.encode())
                .onError(showError)
                .onSuccess(function (data) {
                    var doc = (new DOMParser()).parseFromString(data, "text/html");
                    var error = $('.error', doc).html();

                    if (!error) {
                        searchResults.html($('.catalog', doc).html());
                        $cartbtn.updataEventHandlers();
                        $('.error').html('').addClass('hidden');
                    } else {
                        showError(error);
                    }
                });
        }

        closeBtn.on('click', function () {
            toggleAdvancedSearch();
        });

        searchTabs.on('click', function () {
            var parent = this.parent();
            searchTabs.each(function (el) {
                if (el.attr('data-id') === 'more') {return;}
                el.parent().removeClass('active');
            });
            toggleAdvancedSearch(this.attr('data-id') === 'more');
            if (this.attr('data-id') !== 'more') {
                parent.addClass('active');
                category.val(this.attr('data-id'));
                updateSearchResults();
            }
        });

        searchTerm.on('keyup', updateSearchResults);
        category.on('change', updateSearchResults);
        orderBy.on('change', updateSearchResults);
        minPrice.on('change', function () {
            minPriceLabel.html(minPrice.val());
            updateSearchResults();
        });
        maxPrice.on('change', function () {
            maxPriceLabel.html(maxPrice.val());
            updateSearchResults();
        });

    }
);
