public class ConnectionVertex {
	int value;
	ConnectionVertex next;

	public ConnectionVertex(int value) {
		this.value = value;
	}

	public ConnectionVertex[] duplicate() {
		ConnectionVertex head = null;
		ConnectionVertex tail = null;

		ConnectionVertex cur = new ConnectionVertex(this.value);
		cur.next = this.next;

		if (cur != null) {
			int val = this.value;
			head = new ConnectionVertex(val);
			tail = head;

			while (cur.next != null) {
				cur = cur.next;
				val = cur.value;
				ConnectionVertex newVertex = new ConnectionVertex(val);
				tail.next = newVertex;
				tail = tail.next;
			}
		}

		ConnectionVertex[] path = new ConnectionVertex[2];
		path[0] = head;
		path[1] = tail;

		return path;
	}
}
