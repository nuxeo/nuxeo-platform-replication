package org.nuxeo.ecm.platform.replication.exporter;

import java.io.IOException;
import java.util.Iterator;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentModelReader;
import org.nuxeo.ecm.core.query.sql.SQLQueryParser;
import org.nuxeo.ecm.core.search.api.client.SearchService;
import org.nuxeo.ecm.core.search.api.client.common.SearchServiceDelegate;
import org.nuxeo.ecm.core.search.api.client.query.impl.ComposedNXQueryImpl;
import org.nuxeo.ecm.core.search.api.client.search.results.document.SearchPageProvider;

public class ReplicationReader extends DocumentModelReader {

	public static final String proxyQuery = "SELECT * FROM Document WHERE ecm:isProxy = 1";

	public static final String query = "SELECT * FROM Document";

	protected Iterator<DocumentModel> iterator = null;

	protected ReplicationReader(CoreSession session) throws Exception {
		super(session);
		DocumentModelList list = new DocumentModelListImpl(session.query(query));
		list.add(0, session.getRootDocument());
		SearchService service = SearchServiceDelegate.getRemoteSearchService();

		// add proxies
		ComposedNXQueryImpl query = new ComposedNXQueryImpl(SQLQueryParser
				.parse(proxyQuery), service.getSearchPrincipal(session
				.getPrincipal()));
		SearchPageProvider nxqlProvider = new SearchPageProvider(service
				.searchQuery(query, 0, 10), false, null, proxyQuery);
		DocumentModelList proxies = new DocumentModelListImpl(nxqlProvider
				.getCurrentPage());
		while (nxqlProvider.isNextPageAvailable()) {
			proxies.addAll(nxqlProvider.getNextPage());
		}
		for (DocumentModel proxy : proxies) {
			list.add(session.getDocument(proxy.getRef()));
		}
		iterator = list.iterator();
	}

	@Override
	public ExportedDocument read() throws IOException {
		if (iterator.hasNext()) {
			DocumentModel docModel = iterator.next();
			return new ExportedDocumentImpl(docModel, inlineBlobs);

		}
		return null;
	}

}
